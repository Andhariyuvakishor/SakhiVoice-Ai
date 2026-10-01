package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IndianLanguage
import com.example.data.model.UiTextProvider
import com.example.ui.ScreenDestination
import com.example.ui.theme.SakhiCallActiveGreen
import com.example.ui.theme.SakhiEmergencyRed
import com.example.ui.theme.SakhiRoseDark
import com.example.ui.theme.SakhiRoseLight
import com.example.ui.theme.SakhiRosePrimary

@Composable
fun SakhiBottomBar(
    currentScreen: ScreenDestination,
    selectedLanguage: IndianLanguage,
    onNavigate: (ScreenDestination) -> Unit,
    onStartCall: () -> Unit
) {
    val context = LocalContext.current
    val ui = UiTextProvider.get(selectedLanguage)

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        // Home
        NavigationBarItem(
            selected = currentScreen is ScreenDestination.Home,
            onClick = { onNavigate(ScreenDestination.Home) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = ui.home,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = ui.home,
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen is ScreenDestination.Home) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SakhiRosePrimary,
                selectedTextColor = SakhiRosePrimary,
                indicatorColor = SakhiRoseLight
            ),
            modifier = Modifier.testTag("nav_item_home")
        )

        // Secure Voice Call
        NavigationBarItem(
            selected = currentScreen is ScreenDestination.VoiceCall,
            onClick = onStartCall,
            icon = {
                Icon(
                    imageVector = Icons.Default.PhoneInTalk,
                    contentDescription = ui.secureCall,
                    tint = SakhiCallActiveGreen,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = ui.secureCall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SakhiCallActiveGreen
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SakhiCallActiveGreen,
                selectedTextColor = SakhiCallActiveGreen,
                indicatorColor = SakhiCallActiveGreen.copy(alpha = 0.2f)
            ),
            modifier = Modifier.testTag("nav_item_call")
        )

        // Saved Schemes (Offline)
        NavigationBarItem(
            selected = currentScreen is ScreenDestination.Bookmarks,
            onClick = { onNavigate(ScreenDestination.Bookmarks) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = ui.saved,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = ui.saved,
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen is ScreenDestination.Bookmarks) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SakhiRosePrimary,
                selectedTextColor = SakhiRosePrimary,
                indicatorColor = SakhiRoseLight
            ),
            modifier = Modifier.testTag("nav_item_bookmarks")
        )

        // Emergency SOS
        NavigationBarItem(
            selected = false,
            onClick = {
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:181")
                }
                context.startActivity(dialIntent)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Emergency,
                    contentDescription = ui.sos,
                    tint = SakhiEmergencyRed,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = ui.sos,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SakhiEmergencyRed
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = SakhiEmergencyRed.copy(alpha = 0.2f)
            ),
            modifier = Modifier.testTag("nav_item_sos")
        )
    }
}
