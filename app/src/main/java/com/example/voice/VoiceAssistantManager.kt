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
import android.speech.tts.Voice
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
    private var pendingTextToSpeak: String? = null
    private var pendingCompletionCallback: (() -> Unit)? = null
    private var currentLanguage: IndianLanguage = IndianLanguage.ENGLISH

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _rmsAmplitude = MutableStateFlow(0.1f)
    val rmsAmplitude: StateFlow<Float> = _rmsAmplitude.asStateFlow()

    private val _speechRecognitionResult = MutableStateFlow<String?>(null)
    val speechRecognitionResult: StateFlow<String?> = _speechRecognitionResult.asStateFlow()

    private val _livePartialSpeech = MutableStateFlow("")
    val livePartialSpeech: StateFlow<String> = _livePartialSpeech.asStateFlow()

    private val _speechErrorMessage = MutableStateFlow<String?>(null)
    val speechErrorMessage: StateFlow<String?> = _speechErrorMessage.asStateFlow()

    private val _isSpeakerphoneOn = MutableStateFlow(true)
    val isSpeakerphoneOn: StateFlow<Boolean> = _isSpeakerphoneOn.asStateFlow()

    private val mainHandler = Handler(Looper.getMainLooper())
    private var waveAnimationRunnable: Runnable? = null

    init {
        textToSpeech = TextToSpeech(context.applicationContext, this)
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            configureTtsParameters()
            setLanguage(currentLanguage)

            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                    startSpeakingWaveAnimation()
                }

                override fun onDone(utteranceId: String?) {
                    // Check if it's the final sentence or single utterance
                    if (utteranceId != null && !utteranceId.contains("_chunk_part_")) {
                        finishSpeaking()
                    } else if (utteranceId != null && utteranceId.endsWith("_last")) {
                        finishSpeaking()
                    }
                }

                override fun onError(utteranceId: String?) {
                    finishSpeaking()
                }
            })

            // If any speech was queued before TTS finished initializing, speak it now
            val queued = pendingTextToSpeak
            if (!queued.isNullOrBlank()) {
                pendingTextToSpeak = null
                speak(queued, pendingCompletionCallback)
            }
        } else {
            Log.e("VoiceAssistantManager", "TextToSpeech engine initialization failed with status $status")
        }
    }

    private fun configureTtsParameters() {
        textToSpeech?.setSpeechRate(0.85f) // Calm, distinct, patient delivery for rural women
        textToSpeech?.setPitch(1.05f)      // Friendly, supportive, warm female tone
    }

    private fun finishSpeaking() {
        _isSpeaking.value = false
        stopSpeakingWaveAnimation()
        _rmsAmplitude.value = 0.1f
        mainHandler.post {
            pendingCompletionCallback?.invoke()
            pendingCompletionCallback = null
        }
    }

    private fun startSpeakingWaveAnimation() {
        stopSpeakingWaveAnimation()
        var step = 0
        waveAnimationRunnable = object : Runnable {
            override fun run() {
                if (_isSpeaking.value) {
                    val wave = 0.35f + 0.35f * kotlin.math.sin(step * 0.4f).toFloat().coerceIn(0f, 1f)
                    _rmsAmplitude.value = wave
                    step++
                    mainHandler.postDelayed(this, 100)
                }
            }
        }
        mainHandler.post(waveAnimationRunnable!!)
    }

    private fun stopSpeakingWaveAnimation() {
        waveAnimationRunnable?.let { mainHandler.removeCallbacks(it) }
        waveAnimationRunnable = null
    }

    fun setLanguage(language: IndianLanguage) {
        currentLanguage = language
        if (isTtsInitialized && textToSpeech != null) {
            val tts = textToSpeech!!
            val locale = language.ttsLocale
            var isLocaleSet = false

            val availability = tts.isLanguageAvailable(locale)
            if (availability >= TextToSpeech.LANG_AVAILABLE) {
                tts.language = locale
                isLocaleSet = true

                // Try to find a matching female voice for this locale
                try {
                    val matchingVoice = tts.voices?.firstOrNull { voice ->
                        voice.locale.language == locale.language &&
                                (voice.name.contains("female", ignoreCase = true) ||
                                        voice.name.contains("f0", ignoreCase = true) ||
                                        !voice.isNetworkConnectionRequired)
                    }
                    if (matchingVoice != null) {
                        tts.voice = matchingVoice
                    }
                } catch (e: Exception) {
                    // Ignore voice selection errors
                }
            }

            // Fallback hierarchy if language pack is missing:
            if (!isLocaleSet) {
                // Try Indian English
                val fallbackEnglish = Locale("en", "IN")
                if (tts.isLanguageAvailable(fallbackEnglish) >= TextToSpeech.LANG_AVAILABLE) {
                    tts.language = fallbackEnglish
                } else {
                    // Try Hindi
                    val fallbackHindi = Locale("hi", "IN")
                    if (tts.isLanguageAvailable(fallbackHindi) >= TextToSpeech.LANG_AVAILABLE) {
                        tts.language = fallbackHindi
                    } else {
                        tts.language = Locale.ENGLISH
                    }
                }
            }

            configureTtsParameters()
        }
    }

    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        if (text.isBlank()) return

        stopListening()
        pendingCompletionCallback = onComplete

        if (!isTtsInitialized || textToSpeech == null) {
            pendingTextToSpeak = text
            Log.d("VoiceAssistantManager", "TTS not initialized yet. Queuing text: ${text.take(30)}...")
            return
        }

        // Clean formatting: remove asterisks, hash marks, and bullets for clean speech output
        val cleanText = text
            .replace(Regex("[*#_`~>|]"), "")
            .replace(Regex("•\\s*"), "")
            .trim()

        val utteranceBaseId = "sakhi_${System.currentTimeMillis()}"
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceBaseId)

        _isSpeaking.value = true
        startSpeakingWaveAnimation()

        // Break into sentence chunks if text is long to prevent TTS buffer limits
        if (cleanText.length > 350) {
            val sentences = cleanText.split(Regex("(?<=[.!?।\n])\\s+")).filter { it.isNotBlank() }
            if (sentences.isNotEmpty()) {
                sentences.forEachIndexed { index, sentence ->
                    val isLast = index == sentences.lastIndex
                    val chunkId = if (isLast) "${utteranceBaseId}_last" else "${utteranceBaseId}_chunk_part_$index"
                    val chunkParams = Bundle()
                    chunkParams.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, chunkId)
                    val queueMode = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
                    textToSpeech?.speak(sentence, queueMode, chunkParams, chunkId)
                }
            } else {
                textToSpeech?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, utteranceBaseId)
            }
        } else {
            textToSpeech?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, utteranceBaseId)
        }
    }

    fun stopSpeaking() {
        if (isTtsInitialized) {
            textToSpeech?.stop()
            finishSpeaking()
        }
    }

    fun startListening(
        language: IndianLanguage = currentLanguage,
        onResult: (String) -> Unit
    ) {
        mainHandler.post {
            stopSpeaking()
            _speechRecognitionResult.value = null
            _livePartialSpeech.value = ""
            _speechErrorMessage.value = null

            val available = SpeechRecognizer.isRecognitionAvailable(context)
            if (!available) {
                Log.w("VoiceAssistantManager", "Speech recognition service not found on device")
                _speechErrorMessage.value = "Speech recognition service is not available on this device."
                return@post
            }

            try {
                speechRecognizer?.destroy()
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            _isListening.value = true
                            _speechErrorMessage.value = null
                        }

                        override fun onBeginningOfSpeech() {
                            _isListening.value = true
                        }

                        override fun onRmsChanged(rmsdB: Float) {
                            val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.15f, 1.0f)
                            _rmsAmplitude.value = normalized
                        }

                        override fun onBufferReceived(buffer: ByteArray?) {}

                        override fun onEndOfSpeech() {
                            _isListening.value = false
                        }

                        override fun onError(error: Int) {
                            _isListening.value = false
                            _rmsAmplitude.value = 0.1f
                            val errorDesc = when (error) {
                                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Check mic permission."
                                SpeechRecognizer.ERROR_CLIENT -> "Speech recognition client error."
                                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required."
                                SpeechRecognizer.ERROR_NETWORK -> "Network issue for speech recognition."
                                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout. Try speaking again."
                                SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please speak clearly into the microphone."
                                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Microphone busy. Please wait a second."
                                SpeechRecognizer.ERROR_SERVER -> "Speech server error. Please retry."
                                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No voice detected. Tap to speak again."
                                else -> "Speech recognition paused (code $error)."
                            }
                            Log.w("VoiceAssistantManager", "SpeechRecognizer error ($error): $errorDesc")
                            _speechErrorMessage.value = errorDesc
                        }

                        override fun onResults(results: Bundle?) {
                            _isListening.value = false
                            _rmsAmplitude.value = 0.1f
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            val recognized = matches?.firstOrNull()?.trim()
                            if (!recognized.isNullOrBlank()) {
                                _speechRecognitionResult.value = recognized
                                _livePartialSpeech.value = recognized
                                onResult(recognized)
                            } else {
                                _speechErrorMessage.value = "No speech detected. Tap microphone and speak."
                            }
                        }

                        override fun onPartialResults(partialResults: Bundle?) {
                            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            val partial = matches?.firstOrNull()
                            if (!partial.isNullOrBlank()) {
                                _livePartialSpeech.value = partial
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
                _speechErrorMessage.value = "Could not start microphone: ${e.message}"
            }
        }
    }

    fun createSpeechIntent(language: IndianLanguage = currentLanguage): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, language.recognitionCode)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language.recognitionCode)
            putExtra(RecognizerIntent.EXTRA_PROMPT, language.zeroLiteracyAudioPrompt)
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
            stopSpeakingWaveAnimation()
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            // Safe cleanup
        }
    }
}
