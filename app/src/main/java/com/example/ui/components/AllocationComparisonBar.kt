package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CalculatedAsset
import com.example.data.model.RebalanceActionType
import com.example.ui.theme.ActionBuyGreen
import com.example.ui.theme.ActionSellRed
import com.example.util.CurrencyFormatter
import com.example.util.Strings

@Composable
fun AllocationComparisonBar(
    item: CalculatedAsset,
    usePersianDigits: Boolean,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val isFrozen = item.asset.isFrozen || item.actionType == RebalanceActionType.FROZEN
    val categoryColor = item.category?.colorHex?.let { hex ->
        try {
            Color(android.graphics.Color.parseColor(hex))
        } catch (e: Exception) {
            MaterialTheme.colorScheme.primary
        }
    } ?: MaterialTheme.colorScheme.primary

    val currentWeightProgress by animateFloatAsState(
        targetValue = (item.currentWeight / 100f).toFloat().coerceIn(0f, 1f),
        animationSpec = tween(600),
        label = "currentWeight"
    )

    val targetWeightProgress by animateFloatAsState(
        targetValue = if (isFrozen) 0f else (item.asset.targetWeight / 100f).toFloat().coerceIn(0f, 1f),
        animationSpec = tween(600),
        label = "targetWeight"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(categoryColor)
                )
                Text(
                    text = item.asset.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "${strings.currentWeight}: ${CurrencyFormatter.formatPercent(item.currentWeight, usePersianDigits)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "|",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = if (isFrozen) strings.frozenAssetBadge else "${strings.targetWeight}: ${CurrencyFormatter.formatPercent(item.asset.targetWeight, usePersianDigits)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isFrozen) Color(0xFF0369A1) else MaterialTheme.colorScheme.primary
                )

                // Deviation Badge
                val deviation = item.weightDeviation
                val (badgeBg, badgeText, badgeColor) = when {
                    isFrozen ->
                        Triple(
                            Color(0xFFE0F2FE),
                            strings.actionFrozenLabel,
                            Color(0xFF0369A1)
                        )
                    item.actionType == RebalanceActionType.BUY ->
                        Triple(
                            com.example.ui.theme.ActionBuyContainer,
                            "${strings.actionBuyLabel} (${CurrencyFormatter.formatPercent(Math.abs(deviation), usePersianDigits)})",
                            ActionBuyGreen
                        )
                    item.actionType == RebalanceActionType.SELL ->
                        Triple(
                            com.example.ui.theme.ActionSellContainer,
                            "${strings.actionSellLabel} (${CurrencyFormatter.formatPercent(Math.abs(deviation), usePersianDigits)})",
                            ActionSellRed
                        )
                    else ->
                        Triple(
                            MaterialTheme.colorScheme.surfaceVariant,
                            strings.actionBalancedLabel,
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                }

                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Dual Progress Bars
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            // Current Allocation Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = strings.currentWeight.take(4),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(36.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(currentWeightProgress)
                            .clip(RoundedCornerShape(4.dp))
                            .background(categoryColor)
                    )
                }
            }

            // Target Allocation Bar
            if (!isFrozen) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = strings.targetWeight.take(4),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(36.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(targetWeightProgress)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                        )
                    }
                }
            }
        }
    }
}
