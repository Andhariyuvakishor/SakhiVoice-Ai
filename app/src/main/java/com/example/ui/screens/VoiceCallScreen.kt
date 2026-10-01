package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SakhiViewModel
import com.example.ui.components.VoiceWaveAnimation
import com.example.ui.theme.SakhiCallActiveGreen
import com.example.ui.theme.SakhiEmergencyRed
import com.example.ui.theme.SakhiMarigoldContainer
import com.example.ui.theme.SakhiMarigoldDark
import com.example.ui.theme.SakhiRoseDark
import com.example.ui.theme.SakhiRosePrimary
import com.example.ui.theme.SakhiTealTertiary

@Composable
fun VoiceCallScreen(viewModel: SakhiViewModel) {
    BackHandler {
        viewModel.endVoiceCall()
    }

    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val callDuration by viewModel.callDurationSeconds.collectAsState()
    val isMuted by viewModel.isCallMuted.collectAsState()
    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val rmsAmplitude by viewModel.voiceManager.rmsAmplitude.collectAsState()
    val isSpeakerOn by viewModel.voiceManager.isSpeakerphoneOn.collectAsState()
    val sakhiResponse by viewModel.sakhiResponse.collectAsState()
    val userQueryText by viewModel.userQueryText.collectAsState()
    val isRuralOptimized by viewModel.isRuralNetworkOptimized.collectAsState()

    val minutes = callDuration / 60
    val seconds = callDuration % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2B0914),
                        Color(0xFF160309)
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .testTag("voice_call_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Security Badge & Call Duration
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Encrypted",
                            tint = SakhiCallActiveGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "256-bit एन्क्रिप्टेड सुरक्षित कॉल",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = timeFormatted,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                // Network optimization indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SignalCellularAlt,
                        contentDescription = "Network Mode",
                        tint = SakhiCallActiveGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isRuralOptimized) "ग्रामीण 2G/3G नेटवर्क मोड सक्रिय" else "मानक नेटवर्क",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Avatar & Dynamic Spectrum Waves
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    VoiceWaveAnimation(
                        isActive = isListening || isSpeaking,
                        amplitude = rmsAmplitude,
                        size = 200.dp,
                        primaryColor = SakhiRosePrimary,
                        secondaryColor = Color(0xFFF59E0B)
                    )

                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = SakhiRosePrimary,
                        shadowElevation = 10.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "सखी",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = when {
                        isSpeaking -> "सखी बोल रही हैं..."
                        isListening -> "सखी आपकी बात सुन रही हैं..."
                        else -> "माइक दबाकर अपनी बोली में बोलिए"
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live Spoken Transcripts Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (userQueryText.isNotBlank()) {
                        Text(
                            text = "आपने कहा: \"$userQueryText\"",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFFFDF78)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Text(
                        text = sakhiResponse,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.clickable { viewModel.repeatCurrentAdvice() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "दोबारा सुनें",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Central In-Call Speak Trigger Button
            Surface(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (isListening) {
                            viewModel.stopListening()
                        } else {
                            viewModel.startListeningForQuery()
                        }
                    }
                    .testTag("call_mic_speak_button"),
                shape = CircleShape,
                color = if (isListening) Color(0xFFE11D48) else SakhiCallActiveGreen,
                shadowElevation = 6.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Speak during call",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom In-Call Action Control Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mute
                CallActionButton(
                    icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    label = if (isMuted) "अनम्यूट" else "म्यूट",
                    isActive = isMuted,
                    onClick = { viewModel.toggleCallMute() },
                    testTag = "call_mute_toggle"
                )

                // Speakerphone
                CallActionButton(
                    icon = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                    label = if (isSpeakerOn) "स्पीकर ऑन" else "ईयरपीस",
                    isActive = isSpeakerOn,
                    onClick = { viewModel.voiceManager.toggleSpeakerphone() },
                    testTag = "call_speaker_toggle"
                )

                // Discreet Safety Disguise (Calculator Switch)
                CallActionButton(
                    icon = Icons.Default.Calculate,
                    label = "गोपनीय पर्दा",
                    isActive = false,
                    onClick = { viewModel.activateEmergencyDisguise() },
                    testTag = "call_discreet_calc"
                )

                // End Call (Red Button)
                Surface(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .clickable { viewModel.endVoiceCall() }
                        .testTag("end_voice_call_button"),
                    shape = CircleShape,
                    color = SakhiEmergencyRed
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CallActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick).testTag(testTag)
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = CircleShape,
            color = if (isActive) Color.White else Color.White.copy(alpha = 0.18f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isActive) Color.Black else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.85f),
            fontWeight = FontWeight.Medium
        )
    }
}
