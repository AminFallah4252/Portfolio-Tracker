package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CashInjectionResult
import com.example.util.CurrencyFormatter
import com.example.util.LocalSoundHaptic
import com.example.util.Strings

@Composable
fun CashInjectionModal(
    currency: String,
    usePersianDigits: Boolean,
    strings: Strings,
    onDismiss: () -> Unit,
    onSimulate: (Double) -> CashInjectionResult,
    onApply: (List<Pair<Int, Double>>) -> Unit
) {
    val soundHaptic = LocalSoundHaptic.current
    var cashStr by remember { mutableStateOf("10000000") } // Default 10M
    val cash = cashStr.toDoubleOrNull() ?: 0.0

    val simulationResult = remember(cash) {
        onSimulate(cash)
    }

    // Asset selection states: assetId -> Boolean
    var selectedAssetIds by remember(simulationResult) {
        mutableStateOf(simulationResult.simulatedAllocations.filter { it.newInvestAmount > 0 || simulationResult.simulatedAllocations.size <= 3 }.map { it.asset.id }.toSet())
    }

    // Custom injected amount overrides (assetId -> customizedAmountStr)
    var customAmounts by remember(simulationResult) {
        mutableStateOf(
            simulationResult.simulatedAllocations.associate {
                it.asset.id to if (it.newInvestAmount > 0) it.newInvestAmount.toLong().toString() else "0"
            }
        )
    }

    // Track which items are in manual edit mode
    var editingAssetId by remember { mutableStateOf<Int?>(null) }

    // Calculate actual total injected
    val totalInjected = simulationResult.simulatedAllocations.sumOf { simItem ->
        if (selectedAssetIds.contains(simItem.asset.id)) {
            customAmounts[simItem.asset.id]?.toDoubleOrNull() ?: 0.0
        } else {
            0.0
        }
    }

    val newPortfolioTotal = (simulationResult.newTotalPortfolioValue - simulationResult.injectionAmount) + totalInjected

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
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
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = strings.cashInjectionTitle,
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

                Text(
                    text = strings.cashInjectionDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Input
                OutlinedTextField(
                    value = cashStr,
                    onValueChange = {
                        cashStr = it
                    },
                    label = { Text("${strings.cashAmount} ($currency)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Quick presets
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(1_000_000.0, 2_000_000.0, 5_000_000.0, 10_000_000.0, 20_000_000.0, 50_000_000.0, 100_000_000.0).forEach { preset ->
                        FilterChip(
                            selected = cash == preset,
                            onClick = {
                                soundHaptic.tap()
                                cashStr = preset.toLong().toString()
                            },
                            label = {
                                Text(
                                    if (preset >= 1_000_000) "${(preset / 1_000_000).toInt()} M" else preset.toString(),
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }

                // Summary banner
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Text(
                                text = "مجموع نقدینگی ورودی:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = CurrencyFormatter.formatCurrency(totalInjected, currency, usePersianDigits),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f, fill = false)) {
                            Text(
                                text = "ارزش جدید پورتفوی:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = CurrencyFormatter.formatCurrency(newPortfolioTotal, currency, usePersianDigits),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "شخصی‌سازی و خرید دارایی‌ها:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(
                        onClick = {
                            soundHaptic.tap()
                            customAmounts = simulationResult.simulatedAllocations.associate {
                                it.asset.id to if (it.newInvestAmount > 0) it.newInvestAmount.toLong().toString() else "0"
                            }
                            selectedAssetIds = simulationResult.simulatedAllocations.filter { it.newInvestAmount > 0 }.map { it.asset.id }.toSet()
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings.smartAllocation, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))

                // Simulated and Customizable List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(simulationResult.simulatedAllocations, key = { it.asset.id }) { simItem ->
                        val catColor = simItem.category?.colorHex?.let {
                            try { Color(android.graphics.Color.parseColor(it)) } catch (e: Exception) { MaterialTheme.colorScheme.primary }
                        } ?: MaterialTheme.colorScheme.primary

                        val isSelected = selectedAssetIds.contains(simItem.asset.id)
                        val enteredAmountStr = customAmounts[simItem.asset.id] ?: "0"
                        val enteredAmount = enteredAmountStr.toDoubleOrNull() ?: 0.0
                        val computedUnits = if (simItem.asset.unitPrice > 0 && isSelected) enteredAmount / simItem.asset.unitPrice else 0.0
                        val isEditing = editingAssetId == simItem.asset.id

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.weight(1f, fill = false)
                                    ) {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = { checked ->
                                                soundHaptic.tap()
                                                selectedAssetIds = if (checked) {
                                                    selectedAssetIds + simItem.asset.id
                                                } else {
                                                    selectedAssetIds - simItem.asset.id
                                                }
                                            },
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(catColor)
                                        )
                                        Text(
                                            text = simItem.asset.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (isSelected) {
                                            Text(
                                                text = "+${CurrencyFormatter.formatCurrency(enteredAmount, currency, usePersianDigits)}",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.primary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                soundHaptic.tap()
                                                editingAssetId = if (isEditing) null else simItem.asset.id
                                                if (!isSelected) {
                                                    selectedAssetIds = selectedAssetIds + simItem.asset.id
                                                }
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Amount",
                                                modifier = Modifier.size(16.dp),
                                                tint = if (isEditing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                // Editable input row when editing
                                AnimatedVisibility(visible = isEditing) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = customAmounts[simItem.asset.id] ?: "",
                                            onValueChange = { newVal ->
                                                customAmounts = customAmounts + (simItem.asset.id to newVal)
                                            },
                                            label = { Text("${strings.cashAmount} ($currency)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                            singleLine = true,
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        IconButton(
                                            onClick = {
                                                soundHaptic.tap()
                                                editingAssetId = null
                                            }
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }

                                if (isSelected && computedUnits > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${strings.actionBuyLabel} ${CurrencyFormatter.formatQuantity(computedUnits, simItem.asset.symbol, usePersianDigits)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${strings.targetWeight}: ${CurrencyFormatter.formatPercent(simItem.targetWeight, usePersianDigits)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Buttons: Cancel and Apply
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            soundHaptic.tap()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(strings.cancel)
                    }

                    Button(
                        onClick = {
                            val itemsToApply = simulationResult.simulatedAllocations
                                .filter { selectedAssetIds.contains(it.asset.id) }
                                .mapNotNull { simItem ->
                                    val amount = customAmounts[simItem.asset.id]?.toDoubleOrNull() ?: 0.0
                                    if (amount > 0 && simItem.asset.unitPrice > 0) {
                                        Pair(simItem.asset.id, amount / simItem.asset.unitPrice)
                                    } else {
                                        null
                                    }
                                }
                            if (itemsToApply.isNotEmpty()) {
                                soundHaptic.successAction()
                                onApply(itemsToApply)
                            }
                            onDismiss()
                        },
                        enabled = totalInjected > 0,
                        modifier = Modifier.weight(1.6f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.applyRebalance)
                    }
                }
            }
        }
    }
}
