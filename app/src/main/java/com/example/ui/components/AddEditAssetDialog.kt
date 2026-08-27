package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Remove
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
import com.example.data.model.AssetCategory
import com.example.data.model.AssetItem
import com.example.util.CurrencyFormatter
import com.example.util.LocalSoundHaptic
import com.example.util.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAssetDialog(
    initialAsset: AssetItem? = null,
    categories: List<AssetCategory>,
    existingAssets: List<AssetItem> = emptyList(),
    currency: String,
    usePersianDigits: Boolean,
    strings: Strings,
    onDismiss: () -> Unit,
    onSave: (name: String, symbol: String, categoryId: Int, quantity: Double, unitPrice: Double, targetWeight: Double, isFrozen: Boolean, frozenPercentage: Double, notes: String) -> Unit,
    onAddNewCategory: () -> Unit
) {
    val soundHaptic = LocalSoundHaptic.current

    var name by remember { mutableStateOf(initialAsset?.name ?: "") }
    var symbol by remember { mutableStateOf(initialAsset?.symbol ?: "") }
    var selectedCategoryId by remember { mutableStateOf(initialAsset?.categoryId ?: categories.firstOrNull()?.id ?: 1) }
    var quantityStr by remember { mutableStateOf(initialAsset?.quantity?.let { CurrencyFormatter.formatSmartFloat(it) } ?: "") }
    var unitPriceStr by remember { mutableStateOf(initialAsset?.unitPrice?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "") }
    var targetWeightStr by remember { mutableStateOf(initialAsset?.targetWeight?.let { CurrencyFormatter.formatSmartFloat(it) } ?: "10.0") }
    var isFrozen by remember { mutableStateOf(initialAsset?.isFrozen ?: false) }
    var frozenPercentageStr by remember {
        mutableStateOf(
            if (initialAsset?.isFrozen == true) CurrencyFormatter.formatSmartFloat(initialAsset.frozenPercentage)
            else "100.0"
        )
    }
    var notes by remember { mutableStateOf(initialAsset?.notes ?: "") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val qty = quantityStr.toDoubleOrNull() ?: 0.0
    val price = unitPriceStr.toDoubleOrNull() ?: 0.0
    val frozenPercent = if (isFrozen) (frozenPercentageStr.toDoubleOrNull() ?: 100.0).coerceIn(0.0, 100.0) else 0.0
    val isFullyFrozen = isFrozen && frozenPercent >= 99.99
    val isPartiallyFrozen = isFrozen && frozenPercent in 0.01..99.98
    val targetWeight = if (isFullyFrozen) 0.0 else (targetWeightStr.toDoubleOrNull() ?: 0.0)
    val calculatedValue = qty * price

    val frozenQty = qty * (frozenPercent / 100.0)
    val releasedQty = qty * ((100.0 - frozenPercent) / 100.0)
    val frozenVal = calculatedValue * (frozenPercent / 100.0)
    val releasedVal = calculatedValue * ((100.0 - frozenPercent) / 100.0)

    val selectedCategory = categories.find { it.id == selectedCategoryId } ?: categories.firstOrNull()

    // Sibling assets in the same category
    val siblingAssets = existingAssets.filter { it.categoryId == selectedCategoryId && it.id != (initialAsset?.id ?: -1) }
    val currentSiblingWeightSum = siblingAssets.filter { !it.isFullyFrozen }.sumOf { it.targetWeight }
    val parentClassTarget = selectedCategory?.targetWeight ?: 100.0
    val maxAllowedWeight = Math.max(0.0, parentClassTarget - currentSiblingWeightSum)
    val isExceedingParent = !isFullyFrozen && targetWeight > (maxAllowedWeight + 0.05) && parentClassTarget > 0

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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (initialAsset == null) strings.addNewAsset else strings.editAsset,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isFrozen) {
                            Surface(
                                color = Color(0xFFE0F2FE),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (isFullyFrozen) strings.frozenAssetBadge else "${strings.frozenAssetBadge} (${CurrencyFormatter.formatSmartFloat(frozenPercent)}%)",
                                    color = Color(0xFF0369A1),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    IconButton(onClick = {
                        soundHaptic.tap()
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = strings.close)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Scrollable Form Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Asset Name & Symbol
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(strings.fieldName) },
                            placeholder = { Text("بیت‌کوین، طلا، تتر...") },
                            modifier = Modifier.weight(1.8f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = symbol,
                            onValueChange = { symbol = it },
                            label = { Text(strings.fieldSymbol) },
                            placeholder = { Text("BTC") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Special Frozen Asset Toggle & Percentage Controls
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isFrozen) Color(0xFFE0F2FE).copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isFrozen) Color(0xFF0284C7) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.AcUnit,
                                            contentDescription = null,
                                            tint = if (isFrozen) Color(0xFF0284C7) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = strings.frozenAssetToggle,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isFrozen) Color(0xFF0369A1) else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = strings.frozenAssetSubtitle,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = isFrozen,
                                    onCheckedChange = {
                                        soundHaptic.tap()
                                        isFrozen = it
                                        if (it) {
                                            if (frozenPercentageStr.toDoubleOrNull() == null || frozenPercentageStr.toDoubleOrNull() == 0.0) {
                                                frozenPercentageStr = "100.0"
                                            }
                                        }
                                    }
                                )
                            }

                            // Partial Freezing Controls (When isFrozen is True)
                            if (isFrozen) {
                                HorizontalDivider(color = Color(0xFF0284C7).copy(alpha = 0.25f))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = strings.frozenPercentage,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF0369A1)
                                    )
                                    Surface(
                                        color = Color(0xFF0284C7).copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = strings.frozenPercentageLabel(frozenPercent),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF0369A1),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                // Interactive Slider for Frozen %
                                Slider(
                                    value = frozenPercent.toFloat().coerceIn(0f, 100f),
                                    onValueChange = {
                                        val rounded = Math.round(it * 10.0) / 10.0
                                        frozenPercentageStr = rounded.toString()
                                    },
                                    valueRange = 0f..100f,
                                    steps = 199,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Direct numerical input for Frozen % + +/- Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            soundHaptic.tap()
                                            val cur = frozenPercentageStr.toDoubleOrNull() ?: 100.0
                                            frozenPercentageStr = Math.max(0.0, Math.round((cur - 5.0) * 10.0) / 10.0).toString()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.height(40.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("5%")
                                    }

                                    OutlinedTextField(
                                        value = frozenPercentageStr,
                                        onValueChange = { frozenPercentageStr = it },
                                        label = { Text(strings.frozenPercentage) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    OutlinedButton(
                                        onClick = {
                                            soundHaptic.tap()
                                            val cur = frozenPercentageStr.toDoubleOrNull() ?: 100.0
                                            frozenPercentageStr = Math.min(100.0, Math.round((cur + 5.0) * 10.0) / 10.0).toString()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.height(40.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("5%")
                                    }
                                }

                                // Quick Percentage Presets
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(25.0, 50.0, 75.0, 100.0).forEach { preset ->
                                        FilterChip(
                                            selected = Math.abs(frozenPercent - preset) < 0.1,
                                            onClick = {
                                                soundHaptic.tap()
                                                frozenPercentageStr = preset.toString()
                                            },
                                            label = {
                                                Text(
                                                    if (preset >= 100.0) "۱۰۰٪ (${strings.actionFrozen})" else "${preset.toInt()}%",
                                                    fontSize = 11.sp,
                                                    maxLines = 1
                                                )
                                            }
                                        )
                                    }
                                }

                                // Visual Breakdown of Frozen vs Liquid Portions
                                Surface(
                                    color = Color.White.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF0369A1), modifier = Modifier.size(13.dp))
                                                Text(text = strings.frozenPortion, style = MaterialTheme.typography.labelSmall, color = Color(0xFF0369A1))
                                            }
                                            Text(
                                                text = "${CurrencyFormatter.formatSmartFloat(frozenQty)} ${symbol.ifBlank { "" }} (${CurrencyFormatter.formatCurrency(frozenVal, currency, usePersianDigits)})",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0369A1)
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(Icons.Default.LockOpen, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(13.dp))
                                                Text(text = strings.releasedPortion, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                            }
                                            Text(
                                                text = "${CurrencyFormatter.formatSmartFloat(releasedQty)} ${symbol.ifBlank { "" }} (${CurrencyFormatter.formatCurrency(releasedVal, currency, usePersianDigits)})",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Category Selector
                    Text(
                        text = strings.fieldCategory,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory?.name ?: "انتخاب دسته‌بندی",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            leadingIcon = {
                                val catColor = selectedCategory?.colorHex?.let {
                                    try { Color(android.graphics.Color.parseColor(it)) } catch (e: Exception) { MaterialTheme.colorScheme.primary }
                                } ?: MaterialTheme.colorScheme.primary
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(catColor)
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            categories.forEach { cat ->
                                val catColor = try {
                                    Color(android.graphics.Color.parseColor(cat.colorHex))
                                } catch (e: Exception) {
                                    MaterialTheme.colorScheme.primary
                                }
                                DropdownMenuItem(
                                    text = { Text(cat.name) },
                                    leadingIcon = {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(catColor)
                                        )
                                    },
                                    onClick = {
                                        soundHaptic.tap()
                                        selectedCategoryId = cat.id
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("+ ${strings.newCategory}", color = MaterialTheme.colorScheme.primary) },
                                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                onClick = {
                                    soundHaptic.tap()
                                    categoryDropdownExpanded = false
                                    onAddNewCategory()
                                }
                            )
                        }
                    }

                    // Quantity and Unit Price
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { quantityStr = it },
                        label = { Text("${strings.fieldQuantity} *") },
                        placeholder = { Text("650 یا 0.000358") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = unitPriceStr,
                        onValueChange = { unitPriceStr = it },
                        label = { Text("${strings.fieldUnitPrice} ($currency) *") },
                        placeholder = { Text("22,293") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Target Weight with Slider + Manual Number Input + Step Buttons
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${strings.fieldTargetWeight}:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (isFullyFrozen) {
                                Text(
                                    text = "0.0% (${strings.actionFrozen})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0369A1)
                                )
                            } else {
                                Text(
                                    text = CurrencyFormatter.formatPercent(targetWeight, usePersianDigits),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isExceedingParent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        if (isFullyFrozen) {
                            Surface(
                                color = Color(0xFFE0F2FE),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = strings.fullyFrozenNote,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF0369A1),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        } else {
                            if (isPartiallyFrozen) {
                                Surface(
                                    color = Color(0xFFE0F2FE).copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = strings.partiallyFrozenHint(100.0 - frozenPercent),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF0369A1),
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }

                            // Slider with live drag
                            Slider(
                                value = targetWeight.toFloat().coerceIn(0f, 100f),
                                onValueChange = {
                                    val rounded = Math.round(it * 10.0) / 10.0
                                    targetWeightStr = rounded.toString()
                                },
                                valueRange = 0f..100f,
                                steps = 199,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Direct Numerical Text Input + Step +/- Controls
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        soundHaptic.tap()
                                        val cur = targetWeightStr.toDoubleOrNull() ?: 0.0
                                        targetWeightStr = Math.max(0.0, Math.round((cur - 1.0) * 10.0) / 10.0).toString()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(44.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = strings.stepDecrease, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("1%")
                                }

                                OutlinedTextField(
                                    value = targetWeightStr,
                                    onValueChange = { targetWeightStr = it },
                                    label = { Text(strings.manualEditValue) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp)
                                )

                                OutlinedButton(
                                    onClick = {
                                        soundHaptic.tap()
                                        val cur = targetWeightStr.toDoubleOrNull() ?: 0.0
                                        targetWeightStr = Math.min(100.0, Math.round((cur + 1.0) * 10.0) / 10.0).toString()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(44.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = strings.stepIncrease, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("1%")
                                }
                            }

                            // Parent Class Target Shortcuts
                            if (selectedCategory != null && selectedCategory.targetWeight > 0) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    FilterChip(
                                        selected = Math.abs(targetWeight - selectedCategory.targetWeight) < 0.1,
                                        onClick = {
                                            soundHaptic.tap()
                                            targetWeightStr = selectedCategory.targetWeight.toString()
                                        },
                                        label = {
                                            Text(
                                                "۱۰۰٪ سهم کلاس (${CurrencyFormatter.formatPercent(selectedCategory.targetWeight, usePersianDigits)})",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                        }
                                    )

                                    if (siblingAssets.isNotEmpty() && maxAllowedWeight > 0 && maxAllowedWeight < selectedCategory.targetWeight) {
                                        FilterChip(
                                            selected = Math.abs(targetWeight - maxAllowedWeight) < 0.1,
                                            onClick = {
                                                soundHaptic.tap()
                                                targetWeightStr = String.format("%.1f", maxAllowedWeight)
                                            },
                                            label = {
                                                Text(
                                                    "باقیمانده کلاس (${CurrencyFormatter.formatPercent(maxAllowedWeight, usePersianDigits)})",
                                                    fontSize = 11.sp,
                                                    maxLines = 1
                                                )
                                            }
                                        )
                                    }
                                }
                            }

                            // Quick presets
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(5.0, 10.0, 15.0, 20.0, 25.0, 30.0, 40.0, 50.0).forEach { preset ->
                                    FilterChip(
                                        selected = Math.abs(targetWeight - preset) < 0.1,
                                        onClick = {
                                            soundHaptic.tap()
                                            targetWeightStr = preset.toString()
                                        },
                                        label = { Text("${preset.toInt()}%", fontSize = 11.sp, maxLines = 1) }
                                    )
                                }
                            }
                        }
                    }

                    // Live Calculated Value Card
                    if (calculatedValue > 0) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = strings.totalValue,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = CurrencyFormatter.formatCurrency(calculatedValue, currency, usePersianDigits),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    // Notes
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text(strings.fieldNotes) },
                        placeholder = { Text("اختیاری...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions
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
                            if (name.isNotBlank()) {
                                soundHaptic.successAction()
                                onSave(
                                    name,
                                    symbol,
                                    selectedCategoryId,
                                    qty,
                                    price,
                                    targetWeight,
                                    isFrozen,
                                    frozenPercent,
                                    notes
                                )
                            }
                        },
                        enabled = name.isNotBlank(),
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (initialAsset == null) strings.add else strings.save)
                    }
                }
            }
        }
    }
}
