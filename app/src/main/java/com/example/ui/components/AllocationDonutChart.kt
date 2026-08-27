package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalculatedAsset
import com.example.ui.theme.ChartColors
import com.example.util.CurrencyFormatter
import kotlin.math.atan2

@Composable
fun AllocationDonutChart(
    assets: List<CalculatedAsset>,
    totalValue: Double,
    currency: String,
    usePersianDigits: Boolean,
    modifier: Modifier = Modifier,
    isPrivacyMode: Boolean = false,
    onAssetSelected: ((CalculatedAsset?) -> Unit)? = null
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(assets) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(durationMillis = 800))
    }

    val validAssets = remember(assets) { assets.filter { it.currentValue > 0 } }

    val angles = remember(validAssets, totalValue) {
        if (totalValue <= 0) return@remember emptyList<Pair<Float, Float>>()
        var currentStart = -90f
        validAssets.map { asset ->
            val sweep = ((asset.currentValue / totalValue) * 360f).toFloat()
            val pair = Pair(currentStart, sweep)
            currentStart += sweep
            pair
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.Center
    ) {
        val outlineColor = MaterialTheme.colorScheme.surfaceVariant
        val primaryColor = MaterialTheme.colorScheme.primary

        Canvas(
            modifier = Modifier
                .size(240.dp)
                .pointerInput(validAssets, angles) {
                    detectTapGestures { tapOffset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val dx = tapOffset.x - center.x
                        val dy = tapOffset.y - center.y
                        val distance = kotlin.math.sqrt(dx * dx + dy * dy)
                        val outerRadius = size.width / 2f
                        val innerRadius = outerRadius - 38.dp.toPx()

                        if (distance in innerRadius..outerRadius && angles.isNotEmpty()) {
                            var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                            // Normalize to -90..270
                            if (angle < -90f) angle += 360f

                            val clickedIdx = angles.indexOfFirst { (start, sweep) ->
                                angle >= start && angle < (start + sweep)
                            }
                            if (clickedIdx != -1) {
                                selectedIndex = if (selectedIndex == clickedIdx) null else clickedIdx
                                onAssetSelected?.invoke(selectedIndex?.let { validAssets.getOrNull(it) })
                            } else {
                                selectedIndex = null
                                onAssetSelected?.invoke(null)
                            }
                        } else {
                            selectedIndex = null
                            onAssetSelected?.invoke(null)
                        }
                    }
                }
        ) {
            val strokeWidth = 32.dp.toPx()
            val strokeWidthSelected = 38.dp.toPx()
            val chartSize = size.minDimension - strokeWidthSelected
            val topLeft = Offset(
                (size.width - chartSize) / 2f,
                (size.height - chartSize) / 2f
            )

            if (validAssets.isEmpty()) {
                // Empty state ring
                drawArc(
                    color = outlineColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(chartSize, chartSize),
                    style = Stroke(width = strokeWidth)
                )
            } else {
                angles.forEachIndexed { index, (startAngle, sweepAngle) ->
                    val isSelected = selectedIndex == index
                    val rawColor = validAssets[index].category?.colorHex?.let { hex ->
                        try {
                            Color(android.graphics.Color.parseColor(hex))
                        } catch (e: Exception) {
                            ChartColors[index % ChartColors.size]
                        }
                    } ?: ChartColors[index % ChartColors.size]

                    val appliedStroke = if (isSelected) strokeWidthSelected else strokeWidth
                    val animatedSweep = sweepAngle * animationProgress.value

                    drawArc(
                        color = rawColor,
                        startAngle = startAngle,
                        sweepAngle = animatedSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(chartSize, chartSize),
                        style = Stroke(width = appliedStroke, cap = StrokeCap.Butt)
                    )
                }
            }
        }

        // Center Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 48.dp)
        ) {
            if (selectedIndex != null && selectedIndex!! < validAssets.size) {
                val selected = validAssets[selectedIndex!!]
                Text(
                    text = selected.asset.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = CurrencyFormatter.formatPercent(selected.currentWeight, usePersianDigits),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = CurrencyFormatter.formatCurrency(selected.currentValue, currency, usePersianDigits, isHidden = isPrivacyMode),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "ارزش کل سبد",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = CurrencyFormatter.formatCurrency(totalValue, currency, usePersianDigits, isHidden = isPrivacyMode),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${validAssets.size} دارایی",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
