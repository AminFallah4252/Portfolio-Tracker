package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.Strings
import kotlinx.coroutines.delay

@Composable
fun AnimatedSplashScreen(
    strings: Strings,
    onSplashFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "splash_scale"
    )

    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "splash_alpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "splash_infinite")
    val rotationAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "splash_rotation"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splash_pulse"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(1600)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Slate 900
                        Color(0xFF1E1B4B), // Indigo 950
                        Color(0xFF022C22)  // Deep Emerald Teal
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .scale(scaleAnim.value)
                .alpha(alphaAnim.value)
        ) {
            // Animated Glowing Outer Donut & Scale Emblem
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                // Background Ambient Glow
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(pulseGlow)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF6366F1).copy(alpha = 0.35f),
                                    Color(0xFF10B981).copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Rotating Multi-Segment Allocation Canvas
                Canvas(
                    modifier = Modifier
                        .size(130.dp)
                        .rotate(rotationAnim)
                ) {
                    val stroke = 8.dp.toPx()
                    val diameter = size.minDimension - stroke
                    val topLeft = Offset(stroke / 2, stroke / 2)
                    val arcSize = Size(diameter, diameter)

                    // 4 Quadrants
                    drawArc(
                        color = Color(0xFF10B981), // Emerald
                        startAngle = 0f,
                        sweepAngle = 80f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = Color(0xFF818CF8), // Indigo
                        startAngle = 90f,
                        sweepAngle = 80f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = Color(0xFFF59E0B), // Amber
                        startAngle = 180f,
                        sweepAngle = 80f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = Color(0xFF38BDF8), // Sky Blue
                        startAngle = 270f,
                        sweepAngle = 80f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }

                // Center Icon Container
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1E1B4B),
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(76.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Title with High Contrast Typography
            Text(
                text = "بالانس‌پلاس",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    letterSpacing = 0.5.sp
                ),
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFF8FAFC),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // App Subtitle / Tagline
            Text(
                text = "مدیریت و بازتنظیم هوشمند سبد دارایی",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp
                ),
                fontWeight = FontWeight.Medium,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Subtle Loading Indicator Pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dot1Alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f, targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(600, 0), RepeatMode.Reverse),
                    label = "dot1"
                )
                val dot2Alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f, targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(600, 200), RepeatMode.Reverse),
                    label = "dot2"
                )
                val dot3Alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f, targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(600, 400), RepeatMode.Reverse),
                    label = "dot3"
                )

                Box(modifier = Modifier.size(8.dp).clip(CircleShape).alpha(dot1Alpha).background(Color(0xFF10B981)))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).alpha(dot2Alpha).background(Color(0xFF818CF8)))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).alpha(dot3Alpha).background(Color(0xFFF59E0B)))
            }
        }
    }
}
