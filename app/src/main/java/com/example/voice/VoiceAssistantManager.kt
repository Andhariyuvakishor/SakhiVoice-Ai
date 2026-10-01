package com.example.voice

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.data.model.IndianLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class VoiceAssistantManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var audioManager: AudioManager? = null

    private var isTtsInitialized = false
    private var currentLanguage: IndianLanguage = IndianLanguage.HINDI

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _rmsAmplitude = MutableStateFlow(0.1f)
    val rmsAmplitude: StateFlow<Float> = _rmsAmplitude.asStateFlow()

    private val _speechRecognitionResult = MutableStateFlow<String?>(null)
    val speechRecognitionResult: StateFlow<String?> = _speechRecognitionResult.asStateFlow()

    private val _isSpeakerphoneOn = MutableStateFlow(true)
    val isSpeakerphoneOn: StateFlow<Boolean> = _isSpeakerphoneOn.asStateFlow()

    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        textToSpeech = TextToSpeech(context.applicationContext, this)
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            setLanguage(currentLanguage)
            textToSpeech?.setSpeechRate(0.88f) // Patient, distinct, comfortable pace for rural women
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    _rmsAmplitude.value = 0.1f
                }

                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    _rmsAmplitude.value = 0.1f
                }
            })
        } else {
            Log.e("VoiceAssistantManager", "TTS initialization failed")
        }
    }

    fun setLanguage(language: IndianLanguage) {
        currentLanguage = language
        if (isTtsInitialized) {
            val result = textToSpeech?.setLanguage(language.ttsLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to Hindi or English if specific regional voice pack is missing on device
                val fallback = textToSpeech?.setLanguage(Locale("hi", "IN"))
                if (fallback == TextToSpeech.LANG_MISSING_DATA || fallback == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech?.setLanguage(Locale.ENGLISH)
                }
            }
        }
    }

    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        if (!isTtsInitialized) return
        stopListening()
        val utteranceId = "sakhi_utterance_${System.currentTimeMillis()}"
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        _isSpeaking.value = true
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun stopSpeaking() {
        if (isTtsInitialized) {
            textToSpeech?.stop()
            _isSpeaking.value = false
        }
    }

    fun startListening(
        language: IndianLanguage = currentLanguage,
        onResult: (String) -> Unit
    ) {
        mainHandler.post {
            stopSpeaking()
            _speechRecognitionResult.value = null

            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                Log.w("VoiceAssistantManager", "Speech recognition not available on device")
                return@post
            }

            try {
                speechRecognizer?.destroy()
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            _isListening.value = true
                        }

                        override fun onBeginningOfSpeech() {
                            _isListening.value = true
                        }

                        override fun onRmsChanged(rmsdB: Float) {
                            // Normalize rmsdB (-2 to 10) to 0.0 .. 1.0 for visualizer
                            val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.1f, 1.0f)
                            _rmsAmplitude.value = normalized
                        }

                        override fun onBufferReceived(buffer: ByteArray?) {}

                        override fun onEndOfSpeech() {
                            _isListening.value = false
                        }

                        override fun onError(error: Int) {
                            _isListening.value = false
                            _rmsAmplitude.value = 0.1f
                            Log.w("VoiceAssistantManager", "SpeechRecognizer error: $error")
                        }

                        override fun onResults(results: Bundle?) {
                            _isListening.value = false
                            _rmsAmplitude.value = 0.1f
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            val recognized = matches?.firstOrNull()
                            if (!recognized.isNullOrBlank()) {
                                _speechRecognitionResult.value = recognized
                                onResult(recognized)
                            }
                        }

                        override fun onPartialResults(partialResults: Bundle?) {
                            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            val partial = matches?.firstOrNull()
                            if (!partial.isNullOrBlank()) {
                                _speechRecognitionResult.value = partial
                            }
                        }

                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, language.recognitionCode)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language.recognitionCode)
                    putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language.recognitionCode)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                    putExtra(RecognizerIntent.EXTRA_PROMPT, language.zeroLiteracyAudioPrompt)
                }

                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                Log.e("VoiceAssistantManager", "Error starting SpeechRecognizer: ${e.message}")
                _isListening.value = false
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
                _isListening.value = false
                _rmsAmplitude.value = 0.1f
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun toggleSpeakerphone(): Boolean {
        val newState = !_isSpeakerphoneOn.value
        try {
            audioManager?.isSpeakerphoneOn = newState
            _isSpeakerphoneOn.value = newState
        } catch (e: Exception) {
            _isSpeakerphoneOn.value = newState
        }
        return newState
    }

    fun destroy() {
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            // Safe cleanup
        }
    }
}
