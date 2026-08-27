package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AssetCategory
import com.example.util.CurrencyFormatter
import com.example.util.LocalSoundHaptic
import com.example.util.Strings

val AVAILABLE_CATEGORY_ICONS = listOf(
    "gold" to Icons.Default.Savings,
    "trending_up" to Icons.Default.TrendingUp,
    "shield" to Icons.Default.Shield,
    "currency_exchange" to Icons.Default.CurrencyExchange,
    "currency_bitcoin" to Icons.Default.CurrencyBitcoin,
    "account_balance" to Icons.Default.AccountBalance,
    "business" to Icons.Default.Business,
    "home" to Icons.Default.Home,
    "diamond" to Icons.Default.Diamond,
    "category" to Icons.Default.Category,
    "pie_chart" to Icons.Default.PieChart
)

fun getCategoryIconVector(iconName: String): ImageVector {
    return AVAILABLE_CATEGORY_ICONS.find { it.first.equals(iconName, ignoreCase = true) }?.second
        ?: when (iconName.lowercase()) {
            "savings", "monetization_on", "gold" -> Icons.Default.Savings
            "stock", "stocks" -> Icons.Default.TrendingUp
            "security", "fixed", "shield" -> Icons.Default.Shield
            "fx", "forex", "dollar", "currency_exchange" -> Icons.Default.CurrencyExchange
            "crypto", "bitcoin", "currency_bitcoin" -> Icons.Default.CurrencyBitcoin
            "bank", "fiat", "account_balance" -> Icons.Default.AccountBalance
            "home", "real_estate", "apartment" -> Icons.Default.Home
            else -> Icons.Default.Category
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCategoryDialog(
    initialCategory: AssetCategory? = null,
    strings: Strings,
    usePersianDigits: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        colorHex: String,
        iconName: String,
        targetWeight: Double,
        minWeight: Double,
        maxWeight: Double,
        targetTolerance: Double,
        description: String
    ) -> Unit
) {
    val soundHaptic = LocalSoundHaptic.current
    var selectedTab by remember { mutableStateOf(0) } // 0: General, 1: Allocation & Policy

    var name by remember { mutableStateOf(initialCategory?.name ?: "") }
    var selectedColorHex by remember { mutableStateOf(initialCategory?.colorHex ?: "#3B82F6") }
    var selectedIconName by remember { mutableStateOf(initialCategory?.iconName ?: "category") }
    var description by remember { mutableStateOf(initialCategory?.description ?: "") }

    var targetWeightStr by remember { mutableStateOf(initialCategory?.targetWeight?.let { CurrencyFormatter.formatSmartFloat(it) } ?: "15.0") }
    var minWeightStr by remember { mutableStateOf(initialCategory?.minWeight?.let { CurrencyFormatter.formatSmartFloat(it) } ?: "5.0") }
    var maxWeightStr by remember { mutableStateOf(initialCategory?.maxWeight?.let { CurrencyFormatter.formatSmartFloat(it) } ?: "35.0") }
    var targetToleranceStr by remember { mutableStateOf(initialCategory?.targetTolerance?.let { CurrencyFormatter.formatSmartFloat(it) } ?: "0.0") }

    val targetWeight = targetWeightStr.toDoubleOrNull() ?: 0.0
    val minWeight = minWeightStr.toDoubleOrNull() ?: 0.0
    val maxWeight = maxWeightStr.toDoubleOrNull() ?: 100.0
    val targetTolerance = targetToleranceStr.toDoubleOrNull() ?: 0.0

    val presetColors = listOf(
        "#F59E0B", "#3B82F6", "#8B5CF6", "#10B981", "#64748B",
        "#EC4899", "#06B6D4", "#F97316", "#84CC16", "#E11D48",
        "#6366F1", "#14B8A6", "#D97706", "#475569"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val headerColor = try {
                            Color(android.graphics.Color.parseColor(selectedColorHex))
                        } catch (e: Exception) {
                            MaterialTheme.colorScheme.primary
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(headerColor.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getCategoryIconVector(selectedIconName),
                                contentDescription = null,
                                tint = headerColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Text(
                                text = if (initialCategory == null) strings.newCategory else strings.editCategory,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (name.isNotBlank()) name else strings.categoriesTitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    IconButton(onClick = {
                        soundHaptic.tap()
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = strings.close)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Segmented Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            soundHaptic.tap()
                            selectedTab = 0
                        },
                        text = { Text(strings.classGeneralTab, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                        icon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            soundHaptic.tap()
                            selectedTab = 1
                        },
                        text = { Text(strings.classAllocationTab, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                        icon = { Icon(Icons.Default.PieChart, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Form body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (selectedTab == 0) {
                        // General Info Tab
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(strings.categoryName + " *") },
                            placeholder = { Text("طلا، بورس، نقدینگی، املاک...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Description / Strategy
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text(strings.classDescription) },
                            placeholder = { Text(strings.classDescriptionPlaceholder) },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Color selection
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = strings.categoryColor,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(presetColors) { hex ->
                                    val color = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { Color.Gray }
                                    val isSelected = selectedColorHex.equals(hex, ignoreCase = true)

                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .clickable {
                                                soundHaptic.tap()
                                                selectedColorHex = hex
                                            }
                                            .then(
                                                if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                                else Modifier
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Icon selection
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = strings.categoryIcon,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(5),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                            ) {
                                items(AVAILABLE_CATEGORY_ICONS) { (iconKey, vector) ->
                                    val isSelected = selectedIconName.equals(iconKey, ignoreCase = true)
                                    val activeColor = try {
                                        Color(android.graphics.Color.parseColor(selectedColorHex))
                                    } catch (e: Exception) {
                                        MaterialTheme.colorScheme.primary
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) activeColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, activeColor) else null,
                                        modifier = Modifier
                                            .height(54.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                soundHaptic.tap()
                                                selectedIconName = iconKey
                                            }
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Icon(
                                                imageVector = vector,
                                                contentDescription = iconKey,
                                                tint = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Allocation & Policy Tab
                        // 1. Target Weight
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = strings.classTargetWeight,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = strings.classTargetWeightHint,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = CurrencyFormatter.formatPercent(targetWeight, usePersianDigits),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Slider(
                                    value = targetWeight.toFloat().coerceIn(0f, 100f),
                                    onValueChange = { targetWeightStr = (Math.round(it * 10.0) / 10.0).toString() },
                                    valueRange = 0f..100f,
                                    steps = 199,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Direct Text Input + Step buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            soundHaptic.tap()
                                            targetWeightStr = Math.max(0.0, targetWeight - 1.0).toString()
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
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
                                            targetWeightStr = Math.min(100.0, targetWeight + 1.0).toString()
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Text("1%")
                                    }
                                }

                                // Preset Chips
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(0.0, 5.0, 10.0, 15.0, 20.0, 25.0, 30.0, 40.0, 50.0).forEach { preset ->
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

                        // 2. Allocation Bounds (Min & Max)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = strings.classAllocationBounds,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                // Min Weight
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = strings.classMinWeight, style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        text = CurrencyFormatter.formatPercent(minWeight, usePersianDigits),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                                Slider(
                                    value = minWeight.toFloat().coerceIn(0f, maxWeight.toFloat()),
                                    onValueChange = { minWeightStr = (Math.round(it * 10.0) / 10.0).toString() },
                                    valueRange = 0f..100f
                                )

                                // Max Weight
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = strings.classMaxWeight, style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        text = CurrencyFormatter.formatPercent(maxWeight, usePersianDigits),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                Slider(
                                    value = maxWeight.toFloat().coerceIn(minWeight.toFloat(), 100f),
                                    onValueChange = { maxWeightStr = (Math.round(it * 10.0) / 10.0).toString() },
                                    valueRange = 0f..100f
                                )
                            }
                        }

                        // 3. Custom Tolerance
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = strings.classTolerance,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = strings.classToleranceHint,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = if (targetTolerance <= 0.0) "پیش‌فرض" else "±${CurrencyFormatter.formatPercent(targetTolerance, usePersianDigits)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Slider(
                                    value = targetTolerance.toFloat().coerceIn(0f, 5f),
                                    onValueChange = { targetToleranceStr = (Math.round(it * 10.0) / 10.0).toString() },
                                    valueRange = 0f..5f,
                                    steps = 10
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
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
                                    name.trim(),
                                    selectedColorHex,
                                    selectedIconName,
                                    targetWeight,
                                    minWeight,
                                    maxWeight,
                                    targetTolerance,
                                    description.trim()
                                )
                            }
                        },
                        enabled = name.isNotBlank(),
                        modifier = Modifier.weight(1.4f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(strings.save)
                    }
                }
            }
        }
    }
}
