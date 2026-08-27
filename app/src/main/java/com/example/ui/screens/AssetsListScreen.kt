package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssetCategory
import com.example.data.model.CalculatedAsset
import com.example.data.model.RebalanceActionType
import com.example.ui.components.AssetItemCard
import com.example.ui.viewmodel.SortOption
import com.example.util.CurrencyFormatter
import com.example.util.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetsListScreen(
    assets: List<CalculatedAsset>,
    categories: List<AssetCategory>,
    strings: Strings,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategoryFilter: Int?,
    onCategoryFilterChange: (Int?) -> Unit,
    selectedActionFilter: RebalanceActionType?,
    onActionFilterChange: (RebalanceActionType?) -> Unit,
    sortOption: SortOption,
    onSortOptionChange: (SortOption) -> Unit,
    currency: String,
    usePersianDigits: Boolean,
    onAddAsset: () -> Unit,
    onEditAsset: (CalculatedAsset) -> Unit,
    onQuickUpdateAsset: (CalculatedAsset) -> Unit,
    onDeleteAsset: (CalculatedAsset) -> Unit,
    modifier: Modifier = Modifier,
    isPrivacyMode: Boolean = false
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Search & Sort Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text(strings.searchPlaceholder, style = MaterialTheme.typography.bodyMedium) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        AnimatedVisibility(visible = searchQuery.isNotBlank(), enter = fadeIn(), exit = fadeOut()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )
                )

                // Sort Button & Menu
                Box {
                    FilledTonalIconButton(
                        onClick = { sortMenuExpanded = true },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Icon(Icons.Default.Sort, contentDescription = strings.sort)
                    }

                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        SortOption.values().forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.getTitle(strings)) },
                                leadingIcon = {
                                    if (sortOption == option) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    }
                                },
                                onClick = {
                                    onSortOptionChange(option)
                                    sortMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == null,
                        onClick = { onCategoryFilterChange(null) },
                        label = { Text(strings.filterAllCategories) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                items(categories, key = { it.id }) { cat ->
                    val catColor = try {
                        Color(android.graphics.Color.parseColor(cat.colorHex))
                    } catch (e: Exception) {
                        MaterialTheme.colorScheme.primary
                    }

                    FilterChip(
                        selected = selectedCategoryFilter == cat.id,
                        onClick = {
                            onCategoryFilterChange(if (selectedCategoryFilter == cat.id) null else cat.id)
                        },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(catColor)
                            )
                        },
                        label = { Text(cat.name) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Rebalance Action Status Filter Chips (Using LazyRow to prevent horizontal clipping)
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedActionFilter == null,
                        onClick = { onActionFilterChange(null) },
                        label = { Text(strings.filterAllActions) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                item {
                    FilterChip(
                        selected = selectedActionFilter == RebalanceActionType.BUY,
                        onClick = {
                            onActionFilterChange(if (selectedActionFilter == RebalanceActionType.BUY) null else RebalanceActionType.BUY)
                        },
                        label = { Text(strings.actionBuyLabel) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                item {
                    FilterChip(
                        selected = selectedActionFilter == RebalanceActionType.SELL,
                        onClick = {
                            onActionFilterChange(if (selectedActionFilter == RebalanceActionType.SELL) null else RebalanceActionType.SELL)
                        },
                        label = { Text(strings.actionSellLabel) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                item {
                    FilterChip(
                        selected = selectedActionFilter == RebalanceActionType.BALANCED,
                        onClick = {
                            onActionFilterChange(if (selectedActionFilter == RebalanceActionType.BALANCED) null else RebalanceActionType.BALANCED)
                        },
                        label = { Text(strings.actionBalancedLabel) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Asset Count Header & Add Asset Action Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = strings.assetListTitle(assets.size),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    FilledTonalButton(
                        onClick = onAddAsset,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings.add, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Text(
                    text = "${strings.sortBy}: ${sortOption.getTitle(strings)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Empty State
        if (assets.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = strings.emptyAssetsTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = strings.emptyAssetsSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = onAddAsset,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.addNewAsset)
                        }
                    }
                }
            }
        }

        // Asset Items List
        items(assets, key = { it.asset.id }) { item ->
            AssetItemCard(
                item = item,
                currency = currency,
                usePersianDigits = usePersianDigits,
                isPrivacyMode = isPrivacyMode,
                onEdit = { onEditAsset(item) },
                onQuickUpdate = { onQuickUpdateAsset(item) },
                onDelete = { onDeleteAsset(item) }
            )
        }
    }
}
