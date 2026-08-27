package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AssetCategory
import com.example.data.model.AssetItem
import com.example.data.model.PortfolioProfile
import com.example.data.model.PortfolioSnapshot
import com.example.util.DataBackupHelper
import com.example.util.LocalSoundHaptic
import com.example.util.PortfolioBackupPayload
import com.example.util.Strings

@Composable
fun BackupRestoreDialog(
    strings: Strings,
    categories: List<AssetCategory>,
    assets: List<AssetItem>,
    snapshots: List<PortfolioSnapshot>,
    portfolios: List<PortfolioProfile> = emptyList(),
    currency: String,
    tolerancePercent: Double,
    onRestoreData: (PortfolioBackupPayload) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val soundHaptic = LocalSoundHaptic.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Export, 1: Import
    var pastedJsonText by remember { mutableStateOf("") }
    var pendingPayload by remember { mutableStateOf<PortfolioBackupPayload?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // File picker launcher for JSON import
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val content = stream.bufferedReader().readText()
                    val result = DataBackupHelper.parseFromJson(content)
                    result.onSuccess { payload ->
                        soundHaptic.successAction()
                        pendingPayload = payload
                        errorMessage = null
                    }.onFailure { err ->
                        soundHaptic.errorAction()
                        errorMessage = "${strings.importError}: ${err.localizedMessage ?: ""}"
                    }
                }
            } catch (e: Exception) {
                soundHaptic.errorAction()
                errorMessage = "${strings.importError}: ${e.localizedMessage ?: ""}"
            }
        }
    }

    // Helper to generate export JSON
    val exportJsonString = remember(portfolios, categories, assets, snapshots, currency, tolerancePercent) {
        DataBackupHelper.exportToJson(portfolios, categories, assets, snapshots, currency, tolerancePercent)
    }

    fun shareExportFile() {
        soundHaptic.tap()
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, exportJsonString)
            putExtra(Intent.EXTRA_TITLE, "portfolio_backup_${System.currentTimeMillis()}.json")
            type = "application/json"
        }
        val shareIntent = Intent.createChooser(sendIntent, strings.shareBackup)
        context.startActivity(shareIntent)
    }

    fun copyExportToClipboard() {
        soundHaptic.tap()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Portfolio Backup JSON", exportJsonString)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, strings.exportSuccess, Toast.LENGTH_SHORT).show()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
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
                            Icons.Default.Backup,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = strings.backupSection,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = {
                        soundHaptic.tap()
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = strings.close)
                    }
                }

                // Tab Switcher (Export / Import)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            soundHaptic.tap()
                            selectedTab = 0
                            errorMessage = null
                        },
                        text = { Text(strings.exportData, fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            soundHaptic.tap()
                            selectedTab = 1
                            errorMessage = null
                        },
                        text = { Text(strings.importData, fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (selectedTab == 0) {
                        // EXPORT TAB
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "خلاصه محتوای فایل پشتیبان:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (portfolios.isNotEmpty()) {
                                    Text(
                                        text = "• تعداد سبدهای سرمایه‌گذاری: ${portfolios.size} سبد",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Text(
                                    text = "• تعداد دارایی‌ها: ${assets.size} مورد",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "• تعداد کلاس‌های دارایی: ${categories.size} کلاس",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "• رکوردهای تاریخچه و اسنپ‌شات: ${snapshots.size} مورد",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Text(
                            text = strings.exportSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = { shareExportFile() },
                            modifier = Modifier.fillMaxWidth().testTag("export_share_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.shareBackup)
                        }

                        OutlinedButton(
                            onClick = { copyExportToClipboard() },
                            modifier = Modifier.fillMaxWidth().testTag("export_copy_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.copyJson)
                        }
                    } else {
                        // IMPORT TAB
                        Text(
                            text = strings.importSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = {
                                soundHaptic.tap()
                                try {
                                    filePickerLauncher.launch("application/json")
                                } catch (e: Exception) {
                                    filePickerLauncher.launch("*/*")
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("import_file_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("انتخاب فایل پشتیبان (.json)")
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Text(
                            text = strings.pasteJson,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = pastedJsonText,
                            onValueChange = {
                                pastedJsonText = it
                                errorMessage = null
                            },
                            placeholder = { Text("متن فایل JSON را در اینجا جای‌گذاری (Paste) کنید...", fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                soundHaptic.tap()
                                if (pastedJsonText.isNotBlank()) {
                                    val result = DataBackupHelper.parseFromJson(pastedJsonText)
                                    result.onSuccess { payload ->
                                        soundHaptic.successAction()
                                        pendingPayload = payload
                                        errorMessage = null
                                    }.onFailure { err ->
                                        soundHaptic.errorAction()
                                        errorMessage = "${strings.importError}: ${err.localizedMessage ?: ""}"
                                    }
                                }
                            },
                            enabled = pastedJsonText.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().testTag("import_parse_text_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("بررسی و بازیابی متن JSON")
                        }

                        if (errorMessage != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    Text(
                                        text = errorMessage ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom Close button
                TextButton(
                    onClick = {
                        soundHaptic.tap()
                        onDismiss()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(strings.close)
                }
            }
        }
    }

    // Confirmation Dialog before applying imported data
    if (pendingPayload != null) {
        val payload = pendingPayload!!
        AlertDialog(
            onDismissRequest = { pendingPayload = null },
            title = { Text(strings.importConfirmTitle) },
            text = {
                Text(strings.importConfirmText(payload.assets.size, payload.categories.size, payload.snapshots.size))
            },
            confirmButton = {
                Button(
                    onClick = {
                        soundHaptic.successAction()
                        onRestoreData(payload)
                        pendingPayload = null
                        Toast.makeText(context, strings.importSuccess, Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                ) {
                    Text(strings.confirm)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    soundHaptic.tap()
                    pendingPayload = null
                }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}
