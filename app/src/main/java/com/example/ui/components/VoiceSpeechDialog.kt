package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IndianLanguage
import com.example.ui.theme.SakhiMarigoldContainer
import com.example.ui.theme.SakhiMarigoldDark
import com.example.ui.theme.SakhiRoseDark
import com.example.ui.theme.SakhiRoseLight
import com.example.ui.theme.SakhiRosePrimary
import com.example.ui.theme.SakhiTealContainer
import com.example.ui.theme.SakhiTealTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSpeechDialog(
    language: IndianLanguage,
    isListening: Boolean,
    rmsAmplitude: Float,
    livePartialText: String,
    errorMessage: String?,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onSubmitVoiceQuery: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onLaunchSystemSpeechRecognizer: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var textInput by remember { mutableStateOf(livePartialText) }

    ModalBottomSheet(
        onDismissRequest = {
            onStopListening()
            onDismissRequest()
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Speak Your Query to Sakhi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = SakhiRoseDark
                    )
                    Text(
                        text = "Listening in ${language.nativeName} (${language.englishName})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SakhiRosePrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = {
                        onStopListening()
                        onDismissRequest()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Central Listening Wave & Microphone Button
            Box(
                modifier = Modifier.size(170.dp),
                contentAlignment = Alignment.Center
            ) {
                VoiceWaveAnimation(
                    isActive = isListening,
                    amplitude = rmsAmplitude,
                    size = 170.dp
                )

                Surface(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (isListening) {
                                onStopListening()
                            } else {
                                onStartListening()
                            }
                        }
                        .testTag("dialog_mic_button"),
                    shape = CircleShape,
                    color = if (isListening) Color(0xFFBE123C) else SakhiRosePrimary,
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Microphone",
                            tint = Color.White,
                            modifier = Modifier.size(46.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isListening) "Listening... Speak now into the microphone" else "Tap microphone to speak",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (isListening) Color(0xFFBE123C) else SakhiRoseDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Live speech transcription display card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SakhiRoseLight.copy(alpha = 0.4f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    val displayText = if (livePartialText.isNotBlank()) {
                        livePartialText
                    } else if (textInput.isNotBlank()) {
                        textInput
                    } else {
                        "Say e.g.: \"${language.regionalSlangHints.firstOrNull() ?: "How to get gas subsidy?"}\""
                    }

                    Text(
                        text = displayText,
                        fontSize = 16.sp,
                        fontWeight = if (livePartialText.isNotBlank() || textInput.isNotBlank()) FontWeight.Bold else FontWeight.Normal,
                        color = if (livePartialText.isNotBlank() || textInput.isNotBlank()) SakhiRoseDark else Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = errorMessage,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFB91C1C),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Common Quick Speak Dialect Suggestions
            Text(
                text = "Or tap a quick spoken question in ${language.nativeName}:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(language.regionalSlangHints) { hint ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable {
                            onStopListening()
                            onSubmitVoiceQuery(hint)
                            onDismissRequest()
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = SakhiRosePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = hint,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Send Query
            Button(
                onClick = {
                    val query = livePartialText.ifBlank { textInput }
                    if (query.isNotBlank()) {
                        onStopListening()
                        onSubmitVoiceQuery(query)
                        onDismissRequest()
                    }
                },
                enabled = livePartialText.isNotBlank() || textInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SakhiRosePrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("dialog_submit_voice_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Submit Spoken Query",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            androidx.compose.material3.OutlinedButton(
                onClick = {
                    onStopListening()
                    onLaunchSystemSpeechRecognizer()
                    onDismissRequest()
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("system_voice_input_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = SakhiRosePrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Open System Voice Recognition",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SakhiRosePrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
