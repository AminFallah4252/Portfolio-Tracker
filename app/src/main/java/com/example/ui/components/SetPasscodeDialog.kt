package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.util.CurrencyFormatter
import com.example.util.LocalSoundHaptic
import com.example.util.Strings

@Composable
fun SetPasscodeDialog(
    strings: Strings,
    usePersianDigits: Boolean,
    onPasscodeSet: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val soundHaptic = LocalSoundHaptic.current
    var step by remember { mutableStateOf(1) } // 1: Enter PIN, 2: Confirm PIN
    var pin1 by remember { mutableStateOf("") }
    var pin2 by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val currentPin = if (step == 1) pin1 else pin2
    val pinLength = 4

    fun onDigit(digit: String) {
        soundHaptic.keypadTap()
        errorMessage = null
        if (step == 1) {
            if (pin1.length < pinLength) {
                val newPin = pin1 + digit
                pin1 = newPin
                if (newPin.length == pinLength) {
                    soundHaptic.tap()
                    step = 2
                }
            }
        } else {
            if (pin2.length < pinLength) {
                val newPin = pin2 + digit
                pin2 = newPin
                if (newPin.length == pinLength) {
                    if (newPin == pin1) {
                        soundHaptic.successAction()
                        onPasscodeSet(newPin)
                        onDismiss()
                    } else {
                        soundHaptic.warningAction()
                        errorMessage = strings.passcodesDoNotMatch
                        pin2 = ""
                    }
                }
            }
        }
    }

    fun onBackspace() {
        soundHaptic.tap()
        errorMessage = null
        if (step == 1) {
            if (pin1.isNotEmpty()) pin1 = pin1.dropLast(1)
        } else {
            if (pin2.isNotEmpty()) {
                pin2 = pin2.dropLast(1)
            } else {
                step = 1
                pin1 = ""
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = if (step == 1) strings.setPasscode else strings.confirmPasscode,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = strings.close)
                    }
                }

                Text(
                    text = if (step == 1) strings.enterNewPasscode else strings.confirmPasscode,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                // PIN indicator dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    for (i in 0 until pinLength) {
                        val isFilled = i < currentPin.length
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isFilled) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant
                                )
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                // Numeric Keypad
                Column(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val rows = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("", "0", "DEL")
                    )

                    for (row in rows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (key in row) {
                                if (key.isEmpty()) {
                                    Spacer(modifier = Modifier.size(60.dp))
                                } else if (key == "DEL") {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clickable { onBackspace() }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.AutoMirrored.Filled.Backspace,
                                                contentDescription = "Backspace",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                } else {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clickable { onDigit(key) }
                                            .testTag("pin_key_$key")
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = if (usePersianDigits) CurrencyFormatter.toPersianDigits(key) else key,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
