package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalculatedAsset
import com.example.data.model.RebalanceActionType
import com.example.ui.theme.ActionBuyGreen
import com.example.ui.theme.ActionSellRed
import com.example.util.CurrencyFormatter
import com.example.util.LocalSoundHaptic
import com.example.util.Strings

@Composable
fun RebalanceActionCard(
    item: CalculatedAsset,
    currency: String,
    usePersianDigits: Boolean,
    strings: Strings,
    modifier: Modifier = Modifier,
    isPrivacyMode: Boolean = false,
    onQuickEdit: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val soundHaptic = LocalSoundHaptic.current
    val clipboardManager = remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager }

    val categoryColor = item.category?.colorHex?.let { hex ->
        try {
            Color(android.graphics.Color.parseColor(hex))
        } catch (e: Exception) {
            MaterialTheme.colorScheme.primary
        }
    } ?: MaterialTheme.colorScheme.primary

    val isBuy = item.actionType == RebalanceActionType.BUY
    val isSell = item.actionType == RebalanceActionType.SELL
    val isBalanced = item.actionType == RebalanceActionType.BALANCED
    val isFullyFrozen = item.actionType == RebalanceActionType.FROZEN || item.asset.isFullyFrozen
    val isPartiallyFrozen = item.asset.isPartiallyFrozen

    val (badgeBg, badgeTextColor, badgeLabel, actionIcon) = when {
        isFullyFrozen -> Quad(
            Color(0xFFE0F2FE),
            Color(0xFF0369A1),
            strings.actionFrozenLabel,
            Icons.Default.AcUnit
        )
        isBuy -> Quad(
            com.example.ui.theme.ActionBuyContainer,
            ActionBuyGreen,
            strings.actionBuyLabel,
            Icons.Default.ArrowUpward
        )
        isSell -> Quad(
            com.example.ui.theme.ActionSellContainer,
            ActionSellRed,
            strings.actionSellLabel,
            Icons.Default.ArrowDownward
        )
        else -> Quad(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            strings.actionBalancedLabel,
            Icons.Default.CheckCircle
        )
    }

    val copyValueToClipboard: (String, String) -> Unit = { rawText, label ->
        clipboardManager?.setPrimaryClip(ClipData.newPlainText(label, rawText))
        soundHaptic.successAction()
        Toast.makeText(context, strings.copiedToClipboard(rawText), Toast.LENGTH_SHORT).show()
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(categoryColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(categoryColor)
                        )
                    }
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = item.asset.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (item.asset.symbol.isNotBlank()) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = item.asset.symbol,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        if (item.category != null) {
                            Text(
                                text = item.category.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Action Badge
                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = actionIcon,
                            contentDescription = null,
                            tint = badgeTextColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = badgeLabel,
                            color = badgeTextColor,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // Rebalance Action Details (Copiable on touch!)
            if (isBuy || isSell) {
                val formattedAmount = CurrencyFormatter.formatCurrency(
                    Math.abs(item.rebalanceAmount),
                    currency,
                    usePersianDigits,
                    isHidden = isPrivacyMode
                )
                val rawAmountDigits = Math.round(Math.abs(item.rebalanceAmount)).toString()

                val formattedUnits = CurrencyFormatter.formatQuantity(
                    Math.abs(item.rebalanceUnits),
                    item.asset.symbol,
                    usePersianDigits,
                    isHidden = isPrivacyMode
                )
                val rawUnitsFloat = CurrencyFormatter.formatSmartFloat(Math.abs(item.rebalanceUnits))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Copiable Amount Box
                    Surface(
                        color = badgeBg.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                if (!isPrivacyMode) {
                                    copyValueToClipboard(rawAmountDigits, "Trade Amount")
                                }
                            }
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isBuy) strings.unitsToBuy(item.asset.name, 0.0).substringBefore(" ") else strings.unitsToSell(item.asset.name, 0.0).substringBefore(" "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = strings.tapToCopyHint,
                                    tint = badgeTextColor.copy(alpha = 0.7f),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formattedAmount,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = badgeTextColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = strings.tapToCopyHint,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = badgeTextColor.copy(alpha = 0.8f)
                            )
                        }
                    }

                    if (item.asset.unitPrice > 0) {
                        Spacer(modifier = Modifier.width(8.dp))

                        // Copiable Quantity / Units Box
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    if (!isPrivacyMode) {
                                        copyValueToClipboard(rawUnitsFloat, "Trade Units")
                                    }
                                }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = strings.tapToCopyHint,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = strings.quantity,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = formattedUnits,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = strings.tapToCopyHint,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            } else if (isFullyFrozen) {
                Surface(
                    color = Color(0xFFE0F2FE).copy(alpha = 0.6f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AcUnit,
                            contentDescription = null,
                            tint = Color(0xFF0369A1),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = strings.rebalanceFrozenDisclaimer,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF0369A1)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Stats Matrix: Current Value, Target Value, Weights
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = strings.totalValue,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = CurrencyFormatter.formatCurrency(item.currentValue, currency, usePersianDigits, isHidden = isPrivacyMode),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${strings.currentWeight}: ${CurrencyFormatter.formatPercent(item.currentWeight, usePersianDigits)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (isFullyFrozen) strings.frozenAssetBadge else strings.targetWeight,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isFullyFrozen) "—" else CurrencyFormatter.formatCurrency(item.targetValue, currency, usePersianDigits, isHidden = isPrivacyMode),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isFullyFrozen) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isFullyFrozen) "0.0%" else "${strings.targetWeight}: ${CurrencyFormatter.formatPercent(item.asset.targetWeight, usePersianDigits)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isFullyFrozen) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (onQuickEdit != null) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        soundHaptic.tap()
                        onQuickEdit()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(strings.quickUpdate)
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
