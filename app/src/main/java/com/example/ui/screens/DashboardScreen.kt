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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalculatedAsset
import com.example.data.model.PortfolioProfile
import com.example.data.model.PortfolioSummary
import com.example.data.model.RebalanceActionType
import com.example.ui.components.AllocationComparisonBar
import com.example.ui.components.AllocationDonutChart
import com.example.ui.components.PasscodeUnlockDialog
import com.example.ui.components.PortfolioManageDialog
import com.example.ui.components.RebalanceActionCard
import com.example.ui.theme.ActionBuyGreen
import com.example.ui.theme.ActionSellRed
import com.example.util.CurrencyFormatter
import com.example.util.LocalSoundHaptic
import com.example.util.Strings

@Composable
fun DashboardScreen(
    summary: PortfolioSummary,
    portfolios: List<PortfolioProfile>,
    activePortfolioId: Int,
    strings: Strings,
    currency: String,
    usePersianDigits: Boolean,
    onSelectPortfolio: (Int) -> Unit,
    onCreatePortfolio: (name: String, description: String, icon: String, colorHex: String) -> Unit,
    onEditPortfolio: (id: Int, name: String, description: String) -> Unit,
    onDeletePortfolio: (Int) -> Unit,
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
    val soundHaptic = LocalSoundHaptic.current
    var showPortfolioDialog by remember { mutableStateOf(false) }
    var selectedChartAsset by remember { mutableStateOf<CalculatedAsset?>(null) }

    val activePortfolio = portfolios.find { it.id == activePortfolioId } ?: portfolios.firstOrNull()

    if (showPortfolioDialog) {
        PortfolioManageDialog(
            portfolios = portfolios,
            activePortfolioId = activePortfolioId,
            strings = strings,
            onDismiss = { showPortfolioDialog = false },
            onSelectPortfolio = onSelectPortfolio,
            onCreatePortfolio = onCreatePortfolio,
            onEditPortfolio = onEditPortfolio,
            onDeletePortfolio = onDeletePortfolio
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // 0. Multi-Portfolio Header Banner
        item {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        soundHaptic.tap()
                        showPortfolioDialog = true
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderSpecial,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = activePortfolio?.name ?: strings.currentPortfolio,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (portfolios.size > 1) {
                                Text(
                                    text = "${portfolios.size} سبد فعال",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = strings.switchPortfolio,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 1. Pragmatic Hero Total Portfolio Value & Net Worth Card
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
                                onClick = {
                                    soundHaptic.tap()
                                    onTogglePrivacyMode()
                                },
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
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f, fill = false)
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
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (summary.frozenAssetsCount > 0) {
                            Surface(
                                color = Color(0xFF0284C7).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.AcUnit, contentDescription = null, tint = Color(0xFF0369A1), modifier = Modifier.size(12.dp))
                                    Text(
                                        text = "${summary.frozenAssetsCount} ${strings.actionFrozen}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = Color(0xFF0369A1),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Frozen Assets Banner (if any exist)
        if (summary.frozenAssetsCount > 0) {
            item {
                Surface(
                    color = Color(0xFFE0F2FE).copy(alpha = 0.55f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AcUnit,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = strings.frozenPortfolioValue,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF0369A1),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = CurrencyFormatter.formatCurrency(summary.frozenAssetsValue, currency, usePersianDigits, isHidden = isPrivacyMode),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0369A1),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = strings.liquidPortfolioValue,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = CurrencyFormatter.formatCurrency(summary.liquidTotalValue, currency, usePersianDigits, isHidden = isPrivacyMode),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
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
                        .clickable {
                            soundHaptic.tap()
                            onNavigateToRebalance()
                        },
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = CurrencyFormatter.formatCurrency(summary.totalBuyAmount, currency, usePersianDigits, isHidden = isPrivacyMode),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ActionBuyGreen,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = strings.assetsNeedBuy(summary.buyCount),
                            style = MaterialTheme.typography.labelSmall,
                            color = ActionBuyGreen.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Sell / Profit Surplus Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            soundHaptic.tap()
                            onNavigateToRebalance()
                        },
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = CurrencyFormatter.formatCurrency(summary.totalSellAmount, currency, usePersianDigits, isHidden = isPrivacyMode),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ActionSellRed,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = strings.assetsNeedSell(summary.sellCount),
                            style = MaterialTheme.typography.labelSmall,
                            color = ActionSellRed.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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
                    onClick = {
                        soundHaptic.tap()
                        onNavigateToRebalance()
                    },
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(vertical = 10.dp, horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Balance, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.smartRebalance, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }

                FilledTonalButton(
                    onClick = {
                        soundHaptic.tap()
                        onOpenCashSimulator()
                    },
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(vertical = 10.dp, horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.cashInjection, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }

                FilledTonalIconButton(
                    onClick = {
                        soundHaptic.successAction()
                        onRecordSnapshot()
                    },
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
                        TextButton(
                            onClick = {
                                soundHaptic.tap()
                                onNavigateToAssets()
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
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
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1
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
                            onClick = {
                                soundHaptic.tap()
                                onNavigateToAssets()
                            },
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
                        TextButton(
                            onClick = {
                                soundHaptic.tap()
                                onNavigateToRebalance()
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(strings.viewFullRebalance, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                items(urgentActions.take(3), key = { it.asset.id }) { asset ->
                    RebalanceActionCard(
                        item = asset,
                        currency = currency,
                        usePersianDigits = usePersianDigits,
                        strings = strings,
                        isPrivacyMode = isPrivacyMode,
                        onQuickEdit = { onQuickEditAsset(asset) }
                    )
                }
            }
        }
    }
}
