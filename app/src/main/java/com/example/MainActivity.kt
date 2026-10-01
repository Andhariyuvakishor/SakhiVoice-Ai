package com.example

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.ui.SakhiViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.LanguageSelectorSheet
import com.example.ui.components.SakhiBottomBar
import com.example.ui.components.SakhiTopBar
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.DiscreetCalculatorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SchemeDetailScreen
import com.example.ui.screens.VoiceCallScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SakhiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: SakhiViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val isDiscreetMode by viewModel.isDiscreetModeEnabled.collectAsState()

    var showLanguageSheet by remember { mutableStateOf(false) }

    // System SpeechRecognizer Intent Fallback Launcher
    val systemSpeechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenMatches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = spokenMatches?.firstOrNull()?.trim()
            if (!spokenText.isNullOrBlank()) {
                viewModel.submitVoiceQuery(spokenText)
            }
        }
    }

    // Audio recording permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted for audio input
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    when (currentScreen) {
        is ScreenDestination.VoiceCall -> {
            VoiceCallScreen(viewModel = viewModel)
        }
        is ScreenDestination.DiscreetCalculator -> {
            DiscreetCalculatorScreen(viewModel = viewModel)
        }
        is ScreenDestination.SchemeDetail -> {
            val scheme = (currentScreen as ScreenDestination.SchemeDetail).schemeId.let {
                viewModel.schemeRepository.getSchemeById(it)
            }
            if (scheme != null) {
                SchemeDetailScreen(scheme = scheme, viewModel = viewModel)
            } else {
                viewModel.navigateBack()
            }
        }
        is ScreenDestination.Bookmarks -> {
            Scaffold(
                bottomBar = {
                    SakhiBottomBar(
                        currentScreen = currentScreen,
                        selectedLanguage = selectedLanguage,
                        onNavigate = { viewModel.navigateTo(it) },
                        onStartCall = { viewModel.startVoiceCall() }
                    )
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    BookmarksScreen(viewModel = viewModel)
                }
            }
        }
        is ScreenDestination.Home -> {
            Scaffold(
                topBar = {
                    SakhiTopBar(
                        selectedLanguage = selectedLanguage,
                        onLanguageClick = { showLanguageSheet = true },
                        onDiscreetCalculatorClick = { viewModel.activateEmergencyDisguise() },
                        isDiscreetMode = isDiscreetMode,
                        onToggleDiscreetMode = { viewModel.toggleDiscreetMode() }
                    )
                },
                bottomBar = {
                    SakhiBottomBar(
                        currentScreen = currentScreen,
                        selectedLanguage = selectedLanguage,
                        onNavigate = { viewModel.navigateTo(it) },
                        onStartCall = { viewModel.startVoiceCall() }
                    )
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    HomeScreen(
                        viewModel = viewModel,
                        onOpenLanguageSheet = { showLanguageSheet = true },
                        onLaunchSystemSpeechRecognizer = {
                            val intent = viewModel.voiceManager.createSpeechIntent(selectedLanguage)
                            try {
                                systemSpeechLauncher.launch(intent)
                            } catch (e: Exception) {
                                viewModel.startListeningForQuery()
                            }
                        }
                    )
                }
            }
        }
    }

    if (showLanguageSheet) {
        LanguageSelectorSheet(
            currentLanguage = selectedLanguage,
            onLanguageSelected = { lang ->
                viewModel.selectLanguage(lang, playGreeting = true)
            },
            onDismissRequest = { showLanguageSheet = false },
            onPreviewAudio = { lang ->
                viewModel.voiceManager.setLanguage(lang)
                viewModel.voiceManager.speak(lang.voiceGreeting)
            }
        )
    }
}
