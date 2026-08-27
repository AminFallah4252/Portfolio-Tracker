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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalculatedAsset
import com.example.data.model.PortfolioSummary
import com.example.data.model.RebalanceActionType
import com.example.ui.components.AllocationComparisonBar
import com.example.ui.components.AllocationDonutChart
import com.example.ui.components.RebalanceActionCard
import com.example.ui.theme.ActionBuyGreen
import com.example.ui.theme.ActionSellRed
import com.example.util.CurrencyFormatter
import com.example.util.Strings

@Composable
fun DashboardScreen(
    summary: PortfolioSummary,
    strings: Strings,
    currency: String,
    usePersianDigits: Boolean,
    onNavigateToAssets: () -> Unit,
    onNavigateToRebalance: () -> Unit,
    onOpenCashSimulator: () -> Unit,
    onNormalizeWeights: () -> Unit,
    onRecordSnapshot: () -> Unit,
    onQuickEditAsset: (CalculatedAsset) -> Unit,
    modifier: Modifier = Modifier,
    isPrivacyMode: Boolean = false,
    onTogglePrivacyMode: () -> Unit = {}
) {
    var selectedChartAsset by remember { mutableStateOf<CalculatedAsset?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // 1. Pragmatic Hero Portfolio Value Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                ),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = strings.totalPortfolioValue,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                            IconButton(
                                onClick = onTogglePrivacyMode,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("dashboard_privacy_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (isPrivacyMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (isPrivacyMode) strings.showValues else strings.hideValues,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = strings.assetsCount(summary.calculatedAssets.size),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = CurrencyFormatter.formatCurrency(summary.totalValue, currency, usePersianDigits, isHidden = isPrivacyMode),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    // Linear Multi-segment Asset Allocation Strip
                    if (summary.calculatedAssets.isNotEmpty() && summary.totalValue > 0) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                            ) {
                                Row(modifier = Modifier.fillMaxSize()) {
                                    val validAssets = summary.calculatedAssets.filter { it.currentValue > 0 }
                                    if (validAssets.isNotEmpty()) {
                                        validAssets.take(6).forEachIndexed { idx, asset ->
                                            val rawFraction = (asset.currentValue / summary.totalValue).toFloat()
                                            val weightFraction = if (rawFraction.isNaN() || rawFraction <= 0f) 0.01f else rawFraction.coerceIn(0.01f, 1f)
                                            val assetColor = asset.category?.colorHex?.let { hex ->
                                                try {
                                                    Color(android.graphics.Color.parseColor(hex))
                                                } catch (e: Exception) {
                                                    com.example.ui.theme.ChartColors[idx % com.example.ui.theme.ChartColors.size]
                                                }
                                            } ?: com.example.ui.theme.ChartColors[idx % com.example.ui.theme.ChartColors.size]

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .weight(weightFraction)
                                                    .background(assetColor)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Target Weights Status Bar
                    val isValid = summary.isTargetWeightValid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isValid) Icons.Default.CheckCircle else Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = if (isValid) ActionBuyGreen else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isValid) strings.targetWeightsValid else strings.targetWeightsInvalid(CurrencyFormatter.formatPercent(summary.totalTargetWeight, usePersianDigits)),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }

        // 2. Pragmatic 2-Column Insight Cards (Buy Needed vs Sell Surplus)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Buy Needed Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onNavigateToRebalance),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        ActionBuyGreen.copy(alpha = 0.25f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = strings.buyNeeded,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = CurrencyFormatter.formatCurrency(summary.totalBuyAmount, currency, usePersianDigits, isHidden = isPrivacyMode),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ActionBuyGreen
                        )
                        Text(
                            text = strings.assetsNeedBuy(summary.buyCount),
                            style = MaterialTheme.typography.labelSmall,
                            color = ActionBuyGreen.copy(alpha = 0.8f)
                        )
                    }
                }

                // Sell / Profit Surplus Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onNavigateToRebalance),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        ActionSellRed.copy(alpha = 0.25f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = strings.sellSurplus,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = CurrencyFormatter.formatCurrency(summary.totalSellAmount, currency, usePersianDigits, isHidden = isPrivacyMode),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ActionSellRed
                        )
                        Text(
                            text = strings.assetsNeedSell(summary.sellCount),
                            style = MaterialTheme.typography.labelSmall,
                            color = ActionSellRed.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // 3. Pragmatic Quick Action Toolbar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onNavigateToRebalance,
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(vertical = 10.dp, horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Balance, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.smartRebalance, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }

                FilledTonalButton(
                    onClick = onOpenCashSimulator,
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(vertical = 10.dp, horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.cashInjection, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }

                FilledTonalIconButton(
                    onClick = onRecordSnapshot,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = strings.takeSnapshot, modifier = Modifier.size(18.dp))
                }
            }
        }

        // 4. Compact Allocation Donut & Category Chips
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.allocationBreakdown,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onNavigateToAssets, contentPadding = PaddingValues(0.dp)) {
                            Text(strings.tabAssets, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AllocationDonutChart(
                        assets = summary.calculatedAssets,
                        totalValue = summary.totalValue,
                        currency = currency,
                        usePersianDigits = usePersianDigits,
                        isPrivacyMode = isPrivacyMode,
                        onAssetSelected = { selectedChartAsset = it }
                    )

                    // Quick Category Pills
                    if (summary.categorySummaries.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(summary.categorySummaries) { catSummary ->
                                val catColor = try {
                                    Color(android.graphics.Color.parseColor(catSummary.category.colorHex))
                                } catch (e: Exception) {
                                    MaterialTheme.colorScheme.primary
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(catColor)
                                        )
                                        Text(
                                            text = "${catSummary.category.name}: ${CurrencyFormatter.formatPercent(catSummary.currentWeight, usePersianDigits)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Urgent Rebalance Priority Actions or Empty State
        if (summary.calculatedAssets.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "سبد دارایی آماده ثبت",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "کلاس‌های دارایی (طلا، بورس، نقدینگی ارزی، کریپتو و...) با اوزان هدف و سقف/کف مجاز آماده هستند. برای شروع، اولین دارایی خود را ثبت کنید.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(
                            onClick = onNavigateToAssets,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.addNewAsset)
                        }
                    }
                }
            }
        } else {
            val urgentActions = summary.calculatedAssets.filter { it.actionType != RebalanceActionType.BALANCED }
            if (urgentActions.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.priorityRebalanceActions,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onNavigateToRebalance, contentPadding = PaddingValues(0.dp)) {
                            Text(strings.viewFullRebalance, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                items(urgentActions.take(3), key = { it.asset.id }) { asset ->
                    RebalanceActionCard(
                        item = asset,
                        currency = currency,
                        usePersianDigits = usePersianDigits,
                        isPrivacyMode = isPrivacyMode,
                        onQuickEdit = { onQuickEditAsset(asset) }
                    )
                }
            }
        }
    }
}
