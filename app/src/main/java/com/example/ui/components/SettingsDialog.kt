package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.util.AppLanguage
import com.example.util.AppThemeMode
import com.example.util.CurrencyFormatter
import com.example.util.LocalSoundHaptic
import com.example.util.Strings

@Composable
fun SettingsDialog(
    strings: Strings,
    appLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    currentCurrency: String,
    onCurrencyChange: (String) -> Unit,
    usePersianDigits: Boolean,
    onPersianDigitsChange: (Boolean) -> Unit,
    tolerancePercent: Double,
    onToleranceChange: (Double) -> Unit,
    isPasscodeEnabled: Boolean,
    onPasscodeToggle: (Boolean) -> Unit,
    onOpenSetPasscode: () -> Unit,
    isBiometricAvailable: Boolean,
    isBiometricEnabled: Boolean,
    onBiometricToggle: (Boolean) -> Unit,
    isSoundEnabled: Boolean,
    onSoundToggle: (Boolean) -> Unit,
    isHapticEnabled: Boolean,
    onHapticToggle: (Boolean) -> Unit,
    onOpenBackupRestore: () -> Unit,
    onOpenHelpTutorial: () -> Unit = {},
    onResetSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    val soundHaptic = LocalSoundHaptic.current
    var showResetConfirm by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.settingsTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = {
                        soundHaptic.tap()
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = strings.close)
                    }
                }

                HorizontalDivider()

                // Security & App Lock Section
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = strings.securitySection,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Passcode Lock Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.passcodeLock,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = strings.passcodeSubtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isPasscodeEnabled,
                                onCheckedChange = { enable ->
                                    soundHaptic.tap()
                                    if (enable) {
                                        onOpenSetPasscode()
                                    } else {
                                        onPasscodeToggle(false)
                                    }
                                },
                                modifier = Modifier.testTag("switch_passcode_lock")
                            )
                        }

                        if (isPasscodeEnabled) {
                            // Change PIN button
                            OutlinedButton(
                                onClick = {
                                    soundHaptic.tap()
                                    onOpenSetPasscode()
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("button_change_pin")
                            ) {
                                Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(strings.changePasscode, style = MaterialTheme.typography.labelMedium)
                            }

                            // Fingerprint / Biometric Switch
                            if (isBiometricAvailable) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = strings.biometricUnlock,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = strings.biometricSubtitle,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Switch(
                                        checked = isBiometricEnabled,
                                        onCheckedChange = {
                                            soundHaptic.tap()
                                            onBiometricToggle(it)
                                        },
                                        modifier = Modifier.testTag("switch_biometric_unlock")
                                    )
                                }
                            }
                        }
                    }
                }

                // Data Backup & Restore Section
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Backup,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = strings.backupSection,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = strings.backupRestoreDesc,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        FilledTonalButton(
                            onClick = {
                                soundHaptic.tap()
                                onOpenBackupRestore()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("button_open_backup_restore")
                        ) {
                            Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.backupRestoreAction, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                // Help & Tutorials Section
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.AutoStories,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = strings.helpSectionTitle,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = strings.helpSectionSubtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        FilledTonalButton(
                            onClick = {
                                soundHaptic.tap()
                                onOpenHelpTutorial()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("button_open_help_tutorials")
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.helpGuideAction, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                // Sound & Haptic Feedback Section
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = strings.soundHapticsSection,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Sound Effects Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.soundEffects,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = strings.soundEffectsSubtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isSoundEnabled,
                                onCheckedChange = {
                                    soundHaptic.tap()
                                    onSoundToggle(it)
                                },
                                modifier = Modifier.testTag("switch_sound_effects")
                            )
                        }

                        // Haptic Feedback Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.hapticFeedback,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = strings.hapticSubtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isHapticEnabled,
                                onCheckedChange = {
                                    soundHaptic.tap()
                                    onHapticToggle(it)
                                },
                                modifier = Modifier.testTag("switch_haptic_feedback")
                            )
                        }
                    }
                }

                // Language
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = strings.languageSetting,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = appLanguage == AppLanguage.PERSIAN,
                            onClick = {
                                soundHaptic.tap()
                                onLanguageChange(AppLanguage.PERSIAN)
                            },
                            label = { Text("فارسی (Persian)") },
                            leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier.weight(1f).testTag("lang_persian")
                        )
                        FilterChip(
                            selected = appLanguage == AppLanguage.ENGLISH,
                            onClick = {
                                soundHaptic.tap()
                                onLanguageChange(AppLanguage.ENGLISH)
                            },
                            label = { Text("English") },
                            leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier.weight(1f).testTag("lang_english")
                        )
                    }
                }

                // Theme Mode
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = strings.themeModeSetting,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = themeMode == AppThemeMode.SYSTEM,
                            onClick = {
                                soundHaptic.tap()
                                onThemeModeChange(AppThemeMode.SYSTEM)
                            },
                            label = { Text(strings.themeSystem) },
                            leadingIcon = { Icon(Icons.Default.SettingsBrightness, contentDescription = null, modifier = Modifier.size(14.dp)) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = themeMode == AppThemeMode.LIGHT,
                            onClick = {
                                soundHaptic.tap()
                                onThemeModeChange(AppThemeMode.LIGHT)
                            },
                            label = { Text(strings.themeLight) },
                            leadingIcon = { Icon(Icons.Default.LightMode, contentDescription = null, modifier = Modifier.size(14.dp)) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = themeMode == AppThemeMode.DARK,
                            onClick = {
                                soundHaptic.tap()
                                onThemeModeChange(AppThemeMode.DARK)
                            },
                            label = { Text(strings.themeDark) },
                            leadingIcon = { Icon(Icons.Default.DarkMode, contentDescription = null, modifier = Modifier.size(14.dp)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Currency Unit
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = strings.currencySetting,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("تومان", "ریال", "$", "€").forEach { curr ->
                            FilterChip(
                                selected = currentCurrency == curr,
                                onClick = {
                                    soundHaptic.tap()
                                    onCurrencyChange(curr)
                                },
                                label = { Text(curr) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Persian Digits Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.persianDigitsSetting,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = strings.persianDigitsSubtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = usePersianDigits,
                        onCheckedChange = {
                            soundHaptic.tap()
                            onPersianDigitsChange(it)
                        }
                    )
                }

                // Tolerance Slider + Manual Input + Step Buttons
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.toleranceSetting,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "±${CurrencyFormatter.formatPercent(tolerancePercent, usePersianDigits)}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = strings.toleranceSubtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                soundHaptic.tap()
                                val newVal = (tolerancePercent - 0.1).coerceIn(0.1, 5.0)
                                onToleranceChange(Math.round(newVal * 10.0) / 10.0)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease")
                        }

                        Slider(
                            value = tolerancePercent.toFloat().coerceIn(0.1f, 5.0f),
                            onValueChange = {
                                onToleranceChange(Math.round(it.toDouble() * 10.0) / 10.0)
                            },
                            valueRange = 0.1f..5.0f,
                            steps = 48,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                soundHaptic.tap()
                                val newVal = (tolerancePercent + 0.1).coerceIn(0.1, 5.0)
                                onToleranceChange(Math.round(newVal * 10.0) / 10.0)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = "Increase")
                        }
                    }
                }

                HorizontalDivider()

                // Reset Settings only (NOT database)
                OutlinedButton(
                    onClick = {
                        soundHaptic.tap()
                        showResetConfirm = true
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.resetSettingsButton)
                }

                Button(
                    onClick = {
                        soundHaptic.successAction()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(strings.applyAndClose)
                }
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text(strings.resetSettingsConfirmTitle) },
            text = { Text(strings.resetSettingsConfirmText) },
            confirmButton = {
                Button(
                    onClick = {
                        soundHaptic.successAction()
                        onResetSettings()
                        showResetConfirm = false
                        onDismiss()
                    }
                ) {
                    Text(strings.resetSettingsConfirmAction)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    soundHaptic.tap()
                    showResetConfirm = false
                }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}
