package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
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

                    // Target Weight with Slider
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
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
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Slider(
                            value = targetWeight.toFloat().coerceIn(0f, 100f),
                            onValueChange = { targetWeightStr = String.format("%.1f", it) },
                            valueRange = 0f..100f,
                            steps = 199,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Quick presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(5.0, 10.0, 15.0, 20.0, 30.0, 45.0).forEach { preset ->
                                FilterChip(
                                    selected = Math.abs(targetWeight - preset) < 0.1,
                                    onClick = { targetWeightStr = preset.toString() },
                                    label = { Text("${preset.toInt()}%", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
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
