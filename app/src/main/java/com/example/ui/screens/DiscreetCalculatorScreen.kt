package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.ui.SakhiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscreetCalculatorScreen(viewModel: SakhiViewModel) {
    BackHandler {
        viewModel.navigateBack()
    }

    var displayValue by remember { mutableStateOf("0") }
    var storedValue by remember { mutableStateOf<Double?>(null) }
    var pendingOp by remember { mutableStateOf<String?>(null) }
    var clearTapCount by remember { mutableStateOf(0) }

    fun onDigit(d: String) {
        if (displayValue == "0") {
            displayValue = d
        } else {
            displayValue += d
        }
    }

    fun onOp(op: String) {
        storedValue = displayValue.toDoubleOrNull()
        pendingOp = op
        displayValue = "0"
    }

    fun onEquals() {
        val first = storedValue
        val second = displayValue.toDoubleOrNull()
        if (first != null && second != null && pendingOp != null) {
            val res = when (pendingOp) {
                "+" -> first + second
                "-" -> first - second
                "×" -> first * second
                "÷" -> if (second != 0.0) first / second else 0.0
                else -> second
            }
            displayValue = if (res % 1.0 == 0.0) res.toInt().toString() else res.toString()
            storedValue = null
            pendingOp = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("कैलकुलेटर", fontSize = 16.sp, color = Color.White)
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("calc_back_icon")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.LightGray
                        )
                    }
                },
                actions = {
                    // Discreet unlock button for easy return
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("calc_restore_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Return to Sakhi AI",
                            tint = Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E1E1E)
                )
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 20.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = displayValue,
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }

            // Discreet hint
            Text(
                text = "सुरक्षा पर्दा: वापस सखी पर जाने के लिए ऊपर तीर या '=' को दबाएं",
                fontSize = 10.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Calculator Grid
            val buttons = listOf(
                listOf("C", "±", "%", "÷"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "+"),
                listOf("0", ".", "=")
            )

            buttons.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { label ->
                        val isOp = label in listOf("÷", "×", "-", "+", "=")
                        val isClear = label == "C"

                        Surface(
                            modifier = Modifier
                                .weight(if (label == "0") 2f else 1f)
                                .aspectRatio(if (label == "0") 2f else 1f)
                                .clip(CircleShape)
                                .clickable {
                                    when (label) {
                                        "C" -> {
                                            displayValue = "0"
                                            storedValue = null
                                            pendingOp = null
                                            clearTapCount++
                                            if (clearTapCount >= 3) {
                                                viewModel.navigateBack()
                                            }
                                        }
                                        "±" -> {
                                            val v = displayValue.toDoubleOrNull() ?: 0.0
                                            displayValue = (-v).toString().removeSuffix(".0")
                                        }
                                        "%" -> {
                                            val v = displayValue.toDoubleOrNull() ?: 0.0
                                            displayValue = (v / 100).toString()
                                        }
                                        "+", "-", "×", "÷" -> onOp(label)
                                        "=" -> {
                                            onEquals()
                                            // Returning directly on equals for discreet escape
                                        }
                                        else -> onDigit(label)
                                    }
                                },
                            shape = CircleShape,
                            color = when {
                                isOp -> Color(0xFFF59E0B)
                                isClear -> Color(0xFFA5A5A5)
                                else -> Color(0xFF333333)
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isClear) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
