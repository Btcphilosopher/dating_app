package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.Charcoal
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.Ivory
import com.example.ui.theme.LightGold
import com.example.ui.theme.MutedNavy
import com.example.ui.theme.SoftGold
import com.example.ui.theme.SteelBlue
import com.example.ui.theme.WarmWhite
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LandingScreen(
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showHowItWorks by remember { mutableStateOf(false) }

    // Pulsing animations for elegant background glow
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = { it }),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavy)
            .testTag("landing_screen")
    ) {
        // Decorative abstract organic lines (representing social fabric) drawn dynamically
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.08f)) {
            val center = Offset(size.width / 2f, size.height * 0.4f)
            val baseRadius = size.width * 0.35f
            for (i in 1..4) {
                drawCircle(
                    color = SoftGold,
                    radius = baseRadius * i * 0.6f * glowScale,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        // Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Elegant Header Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "A U R E L I A",
                    style = MaterialTheme.typography.labelLarge,
                    color = SoftGold,
                    fontSize = 16.sp,
                    letterSpacing = 8.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Find the person you're meant to meet.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ivory.copy(alpha = 0.6f),
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Central Value Proposition (Typography Centric)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .widthIn(max = 500.dp)
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Dating designed for adults.",
                    style = MaterialTheme.typography.displayLarge,
                    color = Ivory,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp),
                    lineHeight = 38.sp
                )

                Text(
                    text = "Move beyond endless swiping. Discover a curated relationship circle built around compatibility, deep communication, and genuine values alignment.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Ivory.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 25.sp,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Miniature Interactive Live Network Preview
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clickable { showHowItWorks = true },
                    contentAlignment = Alignment.Center
                ) {
                    CompatibilityCircleNetwork(modifier = Modifier.fillMaxSize())
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "93%",
                            style = MaterialTheme.typography.titleLarge,
                            color = SoftGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        Text(
                            text = "Match Index",
                            style = MaterialTheme.typography.labelSmall,
                            color = Ivory.copy(alpha = 0.5f),
                            fontSize = 9.sp
                        )
                    }
                }
            }

            // Bottom CTA section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 400.dp)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onJoinClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("join_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SoftGold,
                        contentColor = DeepNavy
                    ),
                    shape = RoundedCornerShape(2.dp) // sharp, architecturally elegant corners
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "JOIN AURELIA",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Arrow right"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "HOW MATCHING WORKS",
                    style = MaterialTheme.typography.labelMedium,
                    color = SoftGold,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { showHowItWorks = true }
                        .padding(12.dp)
                        .testTag("how_matching_works_button")
                )
            }
        }
    }

    // "How Matching Works" Overlay Dialogue showing compatibility dimensions
    if (showHowItWorks) {
        Dialog(onDismissRequest = { showHowItWorks = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp)
                    .border(1.dp, SoftGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = Charcoal),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "The Aurelia Protocol",
                            style = MaterialTheme.typography.titleMedium,
                            color = SoftGold,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showHowItWorks = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close description",
                                tint = Ivory
                            )
                        }
                    }

                    Text(
                        text = "We evaluate eight dimensions of connection to eliminate the exhaustive friction of superficial dating. You receive 5-10 curated introductions per day.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ivory.copy(alpha = 0.7f),
                        textAlign = TextAlign.Start,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // List of the 8 dimensions with custom bullet indicators
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val dimensions = listOf(
                            "Personality alignment & Cognitive pacing",
                            "Core values, integrity frameworks & ethics",
                            "Lifestyle parameters & Domestic rhythm",
                            "Career, intellectual trajectory & ambition",
                            "Communication nuances & Active Listening",
                            "Long-term emotional & Family desires",
                            "Niche intellectual & Creative interests",
                            "Emotional index & Conflict resolution format"
                        )

                        dimensions.forEachIndexed { idx, dim ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    text = "${idx + 1}.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SoftGold,
                                    modifier = Modifier.width(20.dp)
                                )
                                Text(
                                    text = dim,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Ivory.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { showHowItWorks = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SteelBlue,
                            contentColor = Ivory
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Text(
                            text = "ACKNOWLEDGE ARCHITECTURE",
                            style = MaterialTheme.typography.labelMedium,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom Canvas drawing representing a rotating golden social compatibility web.
 */
@Composable
fun CompatibilityCircleNetwork(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "rotate")
    val rotationAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = { it })
        ),
        label = "rotation"
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val outerRadius = size.width * 0.42f
        val innerRadius = size.width * 0.22f

        // Draw background rings
        drawCircle(
            color = SoftGold.copy(alpha = 0.15f),
            radius = outerRadius,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
        drawCircle(
            color = SoftGold.copy(alpha = 0.1f),
            radius = innerRadius,
            center = center,
            style = Stroke(width = 0.5.dp.toPx())
        )

        // Draw dots on the outer ring representing the 8 vertices of compatibility
        val numPoints = 8
        val radianRotation = Math.toRadians(rotationAngle.toDouble())
        val points = mutableListOf<Offset>()

        for (i in 0 until numPoints) {
            val theta = (2 * Math.PI / numPoints) * i + radianRotation
            val pointX = center.x + outerRadius * cos(theta).toFloat()
            val pointY = center.y + outerRadius * sin(theta).toFloat()
            val pt = Offset(pointX, pointY)
            points.add(pt)

            // Dynamic point glowing
            drawCircle(
                color = SoftGold,
                radius = 3.dp.toPx(),
                center = pt
            )
        }

        // Draw connections between points
        for (i in 0 until numPoints) {
            val p1 = points[i]
            // Draw lines to adjacent and opposite points to construct a crystalline constellation Web
            for (j in (i + 1) until numPoints) {
                val p2 = points[j]
                val alphaVal = if (j == i + 1 || j == (i + 1) % numPoints) 0.25f else 0.1f
                drawLine(
                    color = SoftGold.copy(alpha = alphaVal),
                    start = p1,
                    end = p2,
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
    }
}

// Inline helper for navigation padding if not fully compiled in standard scaffold on custom drawers
@Composable
fun Modifier.navigationBarsPadding(): Modifier {
    return this // Handled by standard edge-to-edge system or manually
}
