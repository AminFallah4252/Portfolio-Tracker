package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalculatedAsset
import com.example.data.model.PortfolioSummary
import com.example.data.model.RebalanceActionType
import com.example.ui.components.RebalanceActionCard
import com.example.ui.theme.ActionBuyGreen
import com.example.ui.theme.ActionSellRed
import com.example.util.CurrencyFormatter
import com.example.util.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RebalanceScreen(
    summary: PortfolioSummary,
    strings: Strings,
    currency: String,
    usePersianDigits: Boolean,
    onNormalizeWeights: () -> Unit,
    onOpenCashSimulator: () -> Unit,
    onQuickEditAsset: (CalculatedAsset) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: All, 1: Buys, 2: Sells, 3: Balanced

    val buyItems = remember(summary.calculatedAssets) {
        summary.calculatedAssets.filter { it.actionType == RebalanceActionType.BUY }
    }
    val sellItems = remember(summary.calculatedAssets) {
        summary.calculatedAssets.filter { it.actionType == RebalanceActionType.SELL }
    }
    val balancedItems = remember(summary.calculatedAssets) {
        summary.calculatedAssets.filter { it.actionType == RebalanceActionType.BALANCED }
    }

    val displayItems = when (selectedTab) {
        1 -> buyItems
        2 -> sellItems
        3 -> balancedItems
        else -> summary.calculatedAssets
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // 1. Explanatory Guide Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = strings.rebalanceOverviewTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = strings.rebalanceOverviewSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 2. Overview Stats (Buy Needed vs Sell Generated)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = strings.rebalanceSummaryTotal,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Buy Needed Box
                        Surface(
                            color = com.example.ui.theme.ActionBuyContainer,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = strings.actionBuyLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ActionBuyGreen,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = CurrencyFormatter.formatCurrency(summary.totalBuyAmount, currency, usePersianDigits),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ActionBuyGreen
                                )
                                Text(
                                    text = strings.assetsNeedBuy(summary.buyCount),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ActionBuyGreen.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Sell Generated Box
                        Surface(
                            color = com.example.ui.theme.ActionSellContainer,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = strings.actionSellLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ActionSellRed,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = CurrencyFormatter.formatCurrency(summary.totalSellAmount, currency, usePersianDigits),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
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

                    // Cash Injection simulation button
                    FilledTonalButton(
                        onClick = onOpenCashSimulator,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.cashSimulatorTitle, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // 3. Target Weights Normalization Banner (if weights != 100%)
        if (!summary.isTargetWeightValid) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = strings.targetWeightsInvalid(CurrencyFormatter.formatPercent(summary.totalTargetWeight, usePersianDigits)),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = strings.normalizeWeightsHint,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }

                        Button(
                            onClick = onNormalizeWeights,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(strings.autoNormalize)
                        }
                    }
                }
            }
        }

        // 4. Tab Selector (All / Buys / Sells / Balanced)
        item {
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("${strings.filterAll} (${summary.calculatedAssets.size})", style = MaterialTheme.typography.labelMedium) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("${strings.actionBuy} (${buyItems.size})", color = ActionBuyGreen, style = MaterialTheme.typography.labelMedium) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("${strings.actionSell} (${sellItems.size})", color = ActionSellRed, style = MaterialTheme.typography.labelMedium) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("${strings.actionBalanced} (${balancedItems.size})", style = MaterialTheme.typography.labelMedium) }
                )
            }
        }

        // 5. Instruction Title
        item {
            Text(
                text = strings.rebalancePlan,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // 6. Action Items List
        if (displayItems.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "موردی در این دسته وجود ندارد.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(displayItems, key = { it.asset.id }) { asset ->
                RebalanceActionCard(
                    item = asset,
                    currency = currency,
                    usePersianDigits = usePersianDigits,
                    onQuickEdit = { onQuickEditAsset(asset) }
                )
            }
        }
    }
}
