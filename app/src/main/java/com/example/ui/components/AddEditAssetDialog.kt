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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAssetDialog(
    initialAsset: AssetItem? = null,
    categories: List<AssetCategory>,
    existingAssets: List<AssetItem> = emptyList(),
    currency: String,
    usePersianDigits: Boolean,
    onDismiss: () -> Unit,
    onSave: (name: String, symbol: String, categoryId: Int, quantity: Double, unitPrice: Double, targetWeight: Double, notes: String) -> Unit,
    onAddNewCategory: () -> Unit
) {
    var name by remember { mutableStateOf(initialAsset?.name ?: "") }
    var symbol by remember { mutableStateOf(initialAsset?.symbol ?: "") }
    var selectedCategoryId by remember { mutableStateOf(initialAsset?.categoryId ?: categories.firstOrNull()?.id ?: 1) }
    var quantityStr by remember { mutableStateOf(initialAsset?.quantity?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "") }
    var unitPriceStr by remember { mutableStateOf(initialAsset?.unitPrice?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "") }
    var targetWeightStr by remember { mutableStateOf(initialAsset?.targetWeight?.toString() ?: "10.0") }
    var notes by remember { mutableStateOf(initialAsset?.notes ?: "") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val qty = quantityStr.toDoubleOrNull() ?: 0.0
    val price = unitPriceStr.toDoubleOrNull() ?: 0.0
    val targetWeight = targetWeightStr.toDoubleOrNull() ?: 0.0
    val calculatedValue = qty * price

    val selectedCategory = categories.find { it.id == selectedCategoryId } ?: categories.firstOrNull()

    // Sibling assets in the same category (excluding current asset if editing)
    val siblingAssets = existingAssets.filter { it.categoryId == selectedCategoryId && it.id != (initialAsset?.id ?: -1) }
    val currentSiblingWeightSum = siblingAssets.sumOf { it.targetWeight }
    val parentClassTarget = selectedCategory?.targetWeight ?: 100.0
    val maxAllowedWeight = Math.max(0.0, parentClassTarget - currentSiblingWeightSum)
    val isExceedingParent = targetWeight > (maxAllowedWeight + 0.05) && parentClassTarget > 0

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
                    Text(
                        text = if (initialAsset == null) "افزودن دارایی جدید" else "ویرایش دارایی",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
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
                            label = { Text("نام دارایی *") },
                            placeholder = { Text("مثال: طلا بلو، تتر") },
                            modifier = Modifier.weight(1.8f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = symbol,
                            onValueChange = { symbol = it },
                            label = { Text("نماد") },
                            placeholder = { Text("USDT") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Category Selector
                    Text(
                        text = "کلاس / دسته‌بندی دارایی",
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
                                        selectedCategoryId = cat.id
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("+ ایجاد دسته‌بندی جدید", color = MaterialTheme.colorScheme.primary) },
                                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                onClick = {
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
                        label = { Text("موجودی / تعداد *") },
                        placeholder = { Text("مثال: 650 یا 0.000358") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = unitPriceStr,
                        onValueChange = { unitPriceStr = it },
                        label = { Text("قیمت واحد ($currency) *") },
                        placeholder = { Text("مثال: 22,293") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Target Weight with Slider & Parent Target Option
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
                                text = "وزن هدف در سبد:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = CurrencyFormatter.formatPercent(targetWeight, usePersianDigits),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isExceedingParent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }

                        Slider(
                            value = targetWeight.toFloat().coerceIn(0f, 100f),
                            onValueChange = { targetWeightStr = String.format("%.1f", it) },
                            valueRange = 0f..100f,
                            steps = 199,
                            modifier = Modifier.fillMaxWidth()
                        )

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
                                    onClick = { targetWeightStr = selectedCategory.targetWeight.toString() },
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
                                        onClick = { targetWeightStr = String.format("%.1f", maxAllowedWeight) },
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

                        // Warning if exceeding parent class percentage
                        if (isExceedingParent) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "مجموع وزن دارایی‌های این کلاس نباید از سهم کل کلاس (${CurrencyFormatter.formatPercent(parentClassTarget, usePersianDigits)}) بیشتر شود. (حداکثر مجاز: ${CurrencyFormatter.formatPercent(maxAllowedWeight, usePersianDigits)})",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
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
                                    onClick = { targetWeightStr = preset.toString() },
                                    label = { Text("${preset.toInt()}%", fontSize = 11.sp, maxLines = 1) }
                                )
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
                                    text = "ارزش کل محاسبه‌شده:",
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
                        label = { Text("یادداشت / توضیحات") },
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
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("انصراف")
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(
                                    name,
                                    symbol,
                                    selectedCategoryId,
                                    qty,
                                    price,
                                    targetWeight,
                                    notes
                                )
                            }
                        },
                        enabled = name.isNotBlank(),
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (initialAsset == null) "افزودن به سبد" else "ذخیره تغییرات")
                    }
                }
            }
        }
    }
}
