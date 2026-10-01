package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SakhiDatabase
import com.example.data.local.SchemeBookmarkEntity
import com.example.data.model.GovernmentScheme
import com.example.data.model.IndianLanguage
import com.example.data.remote.GeminiRepository
import com.example.data.repository.SchemeRepository
import com.example.voice.VoiceAssistantManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object Home : ScreenDestination()
    object VoiceCall : ScreenDestination()
    data class SchemeDetail(val schemeId: String) : ScreenDestination()
    object Bookmarks : ScreenDestination()
    object DiscreetCalculator : ScreenDestination()
}

class SakhiViewModel(application: Application) : AndroidViewModel(application) {

    private val database = SakhiDatabase.getDatabase(application)
    val schemeRepository = SchemeRepository(database.bookmarkDao())
    private val geminiRepository = GeminiRepository(schemeRepository)
    val voiceManager = VoiceAssistantManager(application)

    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Home)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(IndianLanguage.HINDI)
    val selectedLanguage: StateFlow<IndianLanguage> = _selectedLanguage.asStateFlow()

    private val _userQueryText = MutableStateFlow("")
    val userQueryText: StateFlow<String> = _userQueryText.asStateFlow()

    private val _sakhiResponse = MutableStateFlow("")
    val sakhiResponse: StateFlow<String> = _sakhiResponse.asStateFlow()

    private val _suggestedSchemes = MutableStateFlow<List<GovernmentScheme>>(schemeRepository.getAllSchemes())
    val suggestedSchemes: StateFlow<List<GovernmentScheme>> = _suggestedSchemes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedScheme = MutableStateFlow<GovernmentScheme?>(null)
    val selectedScheme: StateFlow<GovernmentScheme?> = _selectedScheme.asStateFlow()

    // Call Screen State
    private val _callDurationSeconds = MutableStateFlow(0)
    val callDurationSeconds: StateFlow<Int> = _callDurationSeconds.asStateFlow()

    private val _isCallMuted = MutableStateFlow(false)
    val isCallMuted: StateFlow<Boolean> = _isCallMuted.asStateFlow()

    private val _isDiscreetModeEnabled = MutableStateFlow(true) // Privacy on by default
    val isDiscreetModeEnabled: StateFlow<Boolean> = _isDiscreetModeEnabled.asStateFlow()

    private val _isRuralNetworkOptimized = MutableStateFlow(true)
    val isRuralNetworkOptimized: StateFlow<Boolean> = _isRuralNetworkOptimized.asStateFlow()

    private var callTimerJob: Job? = null

    val bookmarkedSchemes: StateFlow<List<SchemeBookmarkEntity>> = schemeRepository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Greet with default response
        _sakhiResponse.value = _selectedLanguage.value.voiceGreeting
    }

    fun selectLanguage(language: IndianLanguage, playGreeting: Boolean = true) {
        _selectedLanguage.value = language
        voiceManager.setLanguage(language)
        _sakhiResponse.value = language.voiceGreeting
        if (playGreeting) {
            voiceManager.speak(language.voiceGreeting)
        }
    }

    fun navigateTo(destination: ScreenDestination) {
        if (destination is ScreenDestination.SchemeDetail) {
            _selectedScheme.value = schemeRepository.getSchemeById(destination.schemeId)
        }
        _currentScreen.value = destination
    }

    fun navigateBack() {
        voiceManager.stopSpeaking()
        voiceManager.stopListening()
        when (_currentScreen.value) {
            is ScreenDestination.DiscreetCalculator -> _currentScreen.value = ScreenDestination.Home
            is ScreenDestination.VoiceCall -> endVoiceCall()
            is ScreenDestination.SchemeDetail -> _currentScreen.value = ScreenDestination.Home
            is ScreenDestination.Bookmarks -> _currentScreen.value = ScreenDestination.Home
            else -> {}
        }
    }

    fun submitVoiceQuery(voiceText: String) {
        _userQueryText.value = voiceText
        _isLoading.value = true

        viewModelScope.launch {
            val result = geminiRepository.querySakhi(voiceText, _selectedLanguage.value)
            _sakhiResponse.value = result.spokenAdvice
            _suggestedSchemes.value = if (result.suggestedSchemes.isNotEmpty()) {
                result.suggestedSchemes
            } else {
                schemeRepository.findMatchingSchemes(voiceText)
            }
            _isLoading.value = false

            // Auto-speak result so non-literate users hear the answer immediately!
            voiceManager.speak(result.spokenAdvice)
        }
    }

    fun startListeningForQuery() {
        voiceManager.stopSpeaking()
        voiceManager.startListening(_selectedLanguage.value) { recognizedText ->
            submitVoiceQuery(recognizedText)
        }
    }

    fun stopListening() {
        voiceManager.stopListening()
    }

    fun repeatCurrentAdvice() {
        val textToSpeak = _sakhiResponse.value.ifBlank { _selectedLanguage.value.voiceGreeting }
        voiceManager.speak(textToSpeak)
    }

    fun speakScheme(scheme: GovernmentScheme) {
        voiceManager.speak(scheme.voiceAudioText)
    }

    // Call Screen Management
    fun startVoiceCall() {
        _currentScreen.value = ScreenDestination.VoiceCall
        _callDurationSeconds.value = 0
        _isCallMuted.value = false
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _callDurationSeconds.value += 1
            }
        }

        // Welcome on call connect
        val callGreeting = when (_selectedLanguage.value) {
            IndianLanguage.HINDI -> "नमस्ते दीदी, आपकी सखी सुरक्षित लाइन पर उपस्थित है। अपनी कोई भी परेशानी या योजना के बारे में बताइए।"
            IndianLanguage.TAMIL -> "வணக்கம் சகோதரி, பாதுகாப்பான இணைப்பில் சகி பேசுகிறேன். உங்களுக்கு என்ன உதவி வேண்டும்?"
            IndianLanguage.TELUGU -> "నమస్కారం అక్కా, నేను మీ సఖిని. సురక్షిత లైన్ ద్వారా మీతో మాట్లాడుతున్నాను. చెప్పండి అక్కా."
            IndianLanguage.BENGALI -> "নমস্কার দিদি, সখী সুরক্ষিত লাইনে যুক্ত হয়েছে। আপনার সমস্যার কথা নিঃসংকোচে বলুন।"
            IndianLanguage.MARATHI -> "नमस्कार ताई, तुमची सखी सुरक्षित कॉलवर हजर आहे. सांगा ताई, काय मदत करू?"
            else -> _selectedLanguage.value.voiceGreeting
        }
        _sakhiResponse.value = callGreeting
        voiceManager.speak(callGreeting)
    }

    fun endVoiceCall() {
        callTimerJob?.cancel()
        callTimerJob = null
        voiceManager.stopSpeaking()
        voiceManager.stopListening()

        if (_isDiscreetModeEnabled.value) {
            _userQueryText.value = ""
        }
        _currentScreen.value = ScreenDestination.Home
    }

    fun toggleCallMute() {
        _isCallMuted.value = !_isCallMuted.value
        if (_isCallMuted.value) {
            voiceManager.stopListening()
        }
    }

    fun toggleDiscreetMode() {
        _isDiscreetModeEnabled.value = !_isDiscreetModeEnabled.value
    }

    fun toggleRuralNetworkOptimization() {
        _isRuralNetworkOptimized.value = !_isRuralNetworkOptimized.value
    }

    fun activateEmergencyDisguise() {
        // Instantly disguise as calculator to shield rural women from prying eyes
        voiceManager.stopSpeaking()
        voiceManager.stopListening()
        _currentScreen.value = ScreenDestination.DiscreetCalculator
    }

    fun toggleBookmark(scheme: GovernmentScheme, isCurrentlyBookmarked: Boolean) {
        viewModelScope.launch {
            schemeRepository.toggleBookmark(scheme, isCurrentlyBookmarked)
        }
    }

    fun toggleDocumentCheck(schemeId: String, docId: String, isChecked: Boolean) {
        viewModelScope.launch {
            schemeRepository.updateDocumentCheck(schemeId, docId, isChecked)
        }
    }

    override fun onCleared() {
        super.onCleared()
        callTimerJob?.cancel()
        voiceManager.destroy()
    }
}
