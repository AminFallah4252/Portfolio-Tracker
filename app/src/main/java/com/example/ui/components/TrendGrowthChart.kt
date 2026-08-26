package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PortfolioSnapshot
import com.example.util.CurrencyFormatter

@Composable
fun TrendGrowthChart(
    snapshots: List<PortfolioSnapshot>,
    currency: String,
    usePersianDigits: Boolean,
    modifier: Modifier = Modifier
) {
    if (snapshots.isEmpty()) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "داده‌ای برای نمایش نمودار روند ثبت نشده است",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    var activeIndex by remember { mutableStateOf<Int?>(null) }
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(snapshots) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, animationSpec = tween(700))
    }

    val sortedSnapshots = remember(snapshots) { snapshots.sortedBy { it.timestamp } }
    val minVal = remember(sortedSnapshots) { sortedSnapshots.minOfOrNull { it.totalValue } ?: 0.0 }
    val maxVal = remember(sortedSnapshots) { sortedSnapshots.maxOfOrNull { it.totalValue } ?: 1.0 }
    val range = remember(minVal, maxVal) { if (maxVal == minVal) 1.0 else (maxVal - minVal) }

    val firstVal = sortedSnapshots.firstOrNull()?.totalValue ?: 0.0
    val lastVal = sortedSnapshots.lastOrNull()?.totalValue ?: 0.0
    val totalGrowth = if (firstVal > 0) ((lastVal - firstVal) / firstVal) * 100.0 else 0.0

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "روند رشد ارزش سبد",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${sortedSnapshots.size} اسنپ‌شات تاریخی",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Growth badge
                val badgeContainer = if (totalGrowth >= 0) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                val badgeText = if (totalGrowth >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

                Surface(
                    color = badgeContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${if (totalGrowth >= 0) "+" else ""}${CurrencyFormatter.formatPercent(totalGrowth, usePersianDigits)}",
                        color = badgeText,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tooltip preview when dragging/tapping
            val displaySnapshot = activeIndex?.let { sortedSnapshots.getOrNull(it) } ?: sortedSnapshots.lastOrNull()
            if (displaySnapshot != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = CurrencyFormatter.formatDate(displaySnapshot.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CurrencyFormatter.formatCurrency(displaySnapshot.totalValue, currency, usePersianDigits),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (displaySnapshot.note.isNotBlank()) {
                    Text(
                        text = displaySnapshot.note,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Interactive Canvas Line & Area Chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .pointerInput(sortedSnapshots) {
                        detectTapGestures(
                            onPress = { offset ->
                                val stepX = size.width / (sortedSnapshots.size - 1).coerceAtLeast(1)
                                val idx = ((offset.x / stepX) + 0.5f).toInt().coerceIn(0, sortedSnapshots.size - 1)
                                activeIndex = idx
                            }
                        )
                    }
                    .pointerInput(sortedSnapshots) {
                        detectDragGestures(
                            onDrag = { change, _ ->
                                val stepX = size.width / (sortedSnapshots.size - 1).coerceAtLeast(1)
                                val idx = ((change.position.x / stepX) + 0.5f).toInt().coerceIn(0, sortedSnapshots.size - 1)
                                activeIndex = idx
                            },
                            onDragEnd = {
                                // keep or reset activeIndex
                            }
                        )
                    }
            ) {
                val width = size.width
                val height = size.height
                val padY = 16.dp.toPx()
                val availableHeight = height - 2 * padY

                val points = sortedSnapshots.mapIndexed { idx, snap ->
                    val x = if (sortedSnapshots.size == 1) width / 2f else (idx.toFloat() / (sortedSnapshots.size - 1)) * width
                    val normalizedY = ((snap.totalValue - minVal) / range).toFloat()
                    val y = height - padY - (normalizedY * availableHeight * animProgress.value)
                    Offset(x, y)
                }

                if (points.size > 1) {
                    val linePath = Path()
                    val fillPath = Path()

                    linePath.moveTo(points.first().x, points.first().y)
                    fillPath.moveTo(points.first().x, height)
                    fillPath.lineTo(points.first().x, points.first().y)

                    for (i in 0 until points.size - 1) {
                        val p0 = points[i]
                        val p1 = points[i + 1]
                        val cx = (p0.x + p1.x) / 2f
                        linePath.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                        fillPath.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                    }

                    fillPath.lineTo(points.last().x, height)
                    fillPath.close()

                    // Draw Gradient Fill
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.35f),
                                primaryColor.copy(alpha = 0.02f)
                            ),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Draw Line Path
                    drawPath(
                        path = linePath,
                        color = primaryColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }

                // Draw Dots
                points.forEachIndexed { idx, pt ->
                    val isSelected = activeIndex == idx || (activeIndex == null && idx == points.size - 1)
                    if (isSelected) {
                        // Highlight Ring
                        drawCircle(
                            color = primaryColor.copy(alpha = 0.2f),
                            radius = 10.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = surfaceColor,
                            radius = 6.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = primaryColor,
                            radius = 4.dp.toPx(),
                            center = pt
                        )
                    } else {
                        drawCircle(
                            color = primaryColor,
                            radius = 3.dp.toPx(),
                            center = pt
                        )
                    }
                }
            }
        }
    }
}
