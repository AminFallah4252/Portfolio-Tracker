package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.CurrencyFormatter
import com.example.util.LocalSoundHaptic
import com.example.util.Strings
import kotlinx.coroutines.delay

@Composable
fun LockScreen(
    strings: Strings,
    usePersianDigits: Boolean,
    isBiometricAvailable: Boolean,
    isBiometricEnabled: Boolean,
    onVerifyPin: (String) -> Boolean,
    onTriggerBiometric: () -> Unit,
    onForgotPasscode: () -> Unit
) {
    val soundHaptic = LocalSoundHaptic.current
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotDialog by remember { mutableStateOf(false) }

    val pinLength = 4

    // Trigger biometric automatically on initial render if enabled
    LaunchedEffect(Unit) {
        if (isBiometricAvailable && isBiometricEnabled) {
            delay(300)
            onTriggerBiometric()
        }
    }

    // Shake animation offset
    val shakeOffset = remember { Animatable(0f) }

    fun handleDigit(digit: String) {
        if (enteredPin.length < pinLength) {
            soundHaptic.keypadTap()
            val newPin = enteredPin + digit
            enteredPin = newPin
            isError = false
            errorMessage = null

            if (newPin.length == pinLength) {
                val success = onVerifyPin(newPin)
                if (success) {
                    soundHaptic.successAction()
                } else {
                    soundHaptic.warningAction()
                    isError = true
                    errorMessage = strings.passcodeIncorrect
                    enteredPin = ""
                }
            }
        }
    }

    fun handleBackspace() {
        if (enteredPin.isNotEmpty()) {
            soundHaptic.tap()
            enteredPin = enteredPin.dropLast(1)
            isError = false
            errorMessage = null
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                // Top Header: Shield / Lock Icon & Titles
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Text(
                        text = strings.appTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = strings.enterPasscode,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // PIN Dots Indicator
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    ) {
                        for (i in 0 until pinLength) {
                            val isFilled = i < enteredPin.length
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isError -> MaterialTheme.colorScheme.error
                                            isFilled -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.outlineVariant
                                        }
                                    )
                            )
                        }
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Center Keypad
                Column(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val rows = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("BIO", "0", "DEL")
                    )

                    for (row in rows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (key in row) {
                                when (key) {
                                    "BIO" -> {
                                        if (isBiometricAvailable && isBiometricEnabled) {
                                            Surface(
                                                shape = CircleShape,
                                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                                modifier = Modifier
                                                    .size(68.dp)
                                                    .clickable { onTriggerBiometric() }
                                                    .testTag("biometric_unlock_button")
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        Icons.Default.Fingerprint,
                                                        contentDescription = strings.biometricUnlock,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(32.dp)
                                                    )
                                                }
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.size(68.dp))
                                        }
                                    }
                                    "DEL" -> {
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            modifier = Modifier
                                                .size(68.dp)
                                                .clickable { handleBackspace() }
                                                .testTag("keypad_del")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.AutoMirrored.Filled.Backspace,
                                                    contentDescription = "Backspace",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }
                                    else -> {
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier
                                                .size(68.dp)
                                                .clickable { handleDigit(key) }
                                                .testTag("keypad_$key")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = if (usePersianDigits) CurrencyFormatter.toPersianDigits(key) else key,
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontSize = 24.sp,
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
                }

                // Bottom actions: Biometric prompt button & Forgot PIN
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    if (isBiometricAvailable && isBiometricEnabled) {
                        TextButton(
                            onClick = onTriggerBiometric,
                            modifier = Modifier.testTag("fingerprint_prompt_text_button")
                        ) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.biometricUnlock, style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    TextButton(onClick = { showForgotDialog = true }) {
                        Text(
                            strings.forgotPasscode,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text(strings.forgotPasscode) },
            text = { Text(strings.resetPasscodeConfirm) },
            confirmButton = {
                Button(
                    onClick = {
                        showForgotDialog = false
                        onForgotPasscode()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(strings.confirm)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}
