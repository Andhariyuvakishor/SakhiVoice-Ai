package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SakhiViewModel
import com.example.ui.ScreenDestination
import com.example.ui.theme.SakhiMarigoldContainer
import com.example.ui.theme.SakhiRoseDark
import com.example.ui.theme.SakhiRosePrimary
import com.example.ui.theme.SakhiSuccessGreen
import com.example.ui.theme.SakhiTealContainer
import com.example.ui.theme.SakhiTealTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(viewModel: SakhiViewModel) {
    BackHandler {
        viewModel.navigateBack()
    }

    val bookmarks by viewModel.bookmarkedSchemes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "सहेजी गई योजनाएं (ऑफलाइन)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = SakhiRoseDark
                        )
                        Text(
                            text = "इंटरनेट न होने पर भी देखें",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("bookmarks_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SakhiRosePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (bookmarks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = SakhiRosePrimary.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "अभी कोई योजना सहेजी नहीं गई है",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SakhiRoseDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "किसी भी योजना के कार्ड पर बुकमार्क आइकन दबाकर उसे बिना इंटरनेट के देखने के लिए सुरक्षित करें।",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SakhiTealContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = SakhiTealTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "यह जानकारी आपके फोन में पूरी तरह सुरक्षित और ऑफलाइन उपलब्ध है।",
                                fontSize = 12.sp,
                                color = SakhiTealTertiary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                items(bookmarks) { item ->
                    val scheme = viewModel.schemeRepository.getSchemeById(item.schemeId)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (scheme != null) {
                                    viewModel.navigateTo(ScreenDestination.SchemeDetail(scheme.id))
                                }
                            }
                            .testTag("saved_item_${item.schemeId}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                Text(
                                    text = item.schemeTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SakhiRoseDark,
                                    modifier = Modifier.weight(1f)
                                )

                                IconButton(
                                    onClick = {
                                        if (scheme != null) {
                                            viewModel.toggleBookmark(scheme, true)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkRemove,
                                        contentDescription = "Remove bookmark",
                                        tint = SakhiRosePrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = item.benefitHighlight,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SakhiSuccessGreen
                            )

                            val checkedCount = item.checkedDocumentsJson
                                .split(",")
                                .count { it.isNotBlank() }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (checkedCount > 0) "$checkedCount कागजात तैयार हैं ✓" else "कागजात जांचें",
                                    fontSize = 11.sp,
                                    color = SakhiTealTertiary,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "विवरण देखें →",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SakhiRosePrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
