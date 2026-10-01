package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GovernmentScheme
import com.example.data.model.IndianLanguage
import com.example.ui.SakhiViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.VoiceWaveAnimation
import com.example.ui.theme.SakhiCallActiveGreen
import com.example.ui.theme.SakhiMarigoldContainer
import com.example.ui.theme.SakhiMarigoldDark
import com.example.ui.theme.SakhiMarigoldSecondary
import com.example.ui.theme.SakhiRoseDark
import com.example.ui.theme.SakhiRoseLight
import com.example.ui.theme.SakhiRosePrimary
import com.example.ui.theme.SakhiSuccessGreen
import com.example.ui.theme.SakhiTealContainer
import com.example.ui.theme.SakhiTealTertiary

@Composable
fun HomeScreen(
    viewModel: SakhiViewModel,
    onOpenLanguageSheet: () -> Unit
) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val rmsAmplitude by viewModel.voiceManager.rmsAmplitude.collectAsState()
    val sakhiResponse by viewModel.sakhiResponse.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userQueryText by viewModel.userQueryText.collectAsState()
    val suggestedSchemes by viewModel.suggestedSchemes.collectAsState()
    val bookmarks by viewModel.bookmarkedSchemes.collectAsState()

    var manualTextInput by remember { mutableStateOf("") }
    var showManualTextEntry by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome & Language audio greeting bar
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("greeting_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = SakhiMarigoldContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "बोलिए अपनी बोली में • ${selectedLanguage.nativeName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = SakhiMarigoldDark
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "शून्य डिजिटल ज्ञान की आवश्यकता। सिर्फ बोलकर योजना जानें।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FilledTonalButton(
                        onClick = { viewModel.repeatCurrentAdvice() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = SakhiRosePrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("listen_greeting_button")
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                            contentDescription = "Listen Greeting",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSpeaking) "रोकें" else "सुनिए",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Giant Pulse Voice Assistant Interaction Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_assistant_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isListening) "सुन रही हूँ... अपनी बात कहिए" else "बटन दबाकर बोलिए",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isListening) SakhiRosePrimary else SakhiRoseDark
                    )

                    Text(
                        text = selectedLanguage.zeroLiteracyAudioPrompt,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Pulse Voice Wave & Big Mic Button
                    Box(
                        modifier = Modifier
                            .size(170.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        VoiceWaveAnimation(
                            isActive = isListening || isSpeaking,
                            amplitude = rmsAmplitude,
                            size = 170.dp
                        )

                        // Central Interactive Mic Button (Accessibility Touch Target > 48dp)
                        Surface(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (isListening) {
                                        viewModel.stopListening()
                                    } else {
                                        viewModel.startListeningForQuery()
                                    }
                                }
                                .testTag("main_mic_button"),
                            shape = CircleShape,
                            color = if (isListening) SakhiRoseDark else SakhiRosePrimary,
                            shadowElevation = 8.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                                    contentDescription = "Tap to speak in your regional language",
                                    tint = Color.White,
                                    modifier = Modifier.size(46.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Spoken user text feedback
                    if (userQueryText.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SakhiTealContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = SakhiTealTertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "\"$userQueryText\"",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SakhiTealTertiary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Secure Voice Call Banner (Requested: "build web or voice call by user network with more security")
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.startVoiceCall() }
                    .testTag("secure_voice_call_banner"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SakhiTealContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SakhiTealTertiary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneInTalk,
                                contentDescription = "Voice Call",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "सखी सुरक्षित आवाज़ कॉल",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF0F3B37)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Encrypted",
                                    tint = SakhiTealTertiary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = "फोन कॉल की तरह सीधे बोलकर बात करें • 256-bit गुप्त लाइन",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.startVoiceCall() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SakhiCallActiveGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text("कॉल करें", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Common Regional Slang & Dialect Question Chips (Click to ask instantly)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "आम बोलचाल में पूछें (Tap to Ask)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${selectedLanguage.nativeName} में",
                        style = MaterialTheme.typography.bodySmall,
                        color = SakhiRosePrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selectedLanguage.regionalSlangHints) { slangPrompt ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable {
                                    viewModel.submitVoiceQuery(slangPrompt)
                                }
                                .testTag("slang_chip_${slangPrompt.take(8)}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = SakhiRosePrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = slangPrompt,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Sakhi AI Advice Card (Spoken guidance)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("advice_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SakhiRoseLight.copy(alpha = 0.45f)
                ),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(SakhiRosePrimary.copy(alpha = 0.3f), SakhiMarigoldSecondary.copy(alpha = 0.3f))))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(SakhiRosePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("स", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "सखी का मार्गदर्शन",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = SakhiRoseDark
                            )
                        }

                        IconButton(
                            onClick = { viewModel.repeatCurrentAdvice() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                contentDescription = "Play/Stop Voice Response",
                                tint = SakhiRosePrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isLoading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = SakhiRosePrimary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "सखी विचार कर रही है...",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            text = sakhiResponse,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            color = SakhiRoseDark
                        )
                    }
                }
            }
        }

        // Suggested / Available Essential Government Schemes
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "प्रमुख सरकारी योजनाएं (${suggestedSchemes.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        items(suggestedSchemes) { scheme ->
            val isBookmarked = bookmarks.any { it.schemeId == scheme.id }
            SchemeSummaryCard(
                scheme = scheme,
                isBookmarked = isBookmarked,
                onBookmarkToggle = { viewModel.toggleBookmark(scheme, isBookmarked) },
                onSpeakScheme = { viewModel.speakScheme(scheme) },
                onClick = {
                    viewModel.navigateTo(ScreenDestination.SchemeDetail(scheme.id))
                }
            )
        }
    }
}

@Composable
fun SchemeSummaryCard(
    scheme: GovernmentScheme,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    onSpeakScheme: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("scheme_card_${scheme.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Category badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SakhiMarigoldContainer)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = scheme.category.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SakhiMarigoldDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = scheme.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SakhiRoseDark
                    )
                }

                Row {
                    IconButton(
                        onClick = onSpeakScheme,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak Scheme",
                            tint = SakhiRosePrimary
                        )
                    }

                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) SakhiRosePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Benefit Highlight Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SakhiSuccessGreen.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SakhiSuccessGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = scheme.benefitHighlight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SakhiSuccessGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = scheme.shortSummary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "आवेदन: ${scheme.whereToApply.take(28)}...",
                    fontSize = 11.sp,
                    color = SakhiRosePrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "पूरी जानकारी →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SakhiRoseDark
                )
            }
        }
    }
}
