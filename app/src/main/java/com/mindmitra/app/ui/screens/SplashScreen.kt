package com.mindmitra.app.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindmitra.app.ui.theme.AccentLavender
import com.mindmitra.app.ui.theme.TextPrimary
import com.mindmitra.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isFemale: Boolean = false,
    onNavigateToHome: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        showContent = true
        delay(2800)
        onNavigateToHome()
    }

    val iconScale by animateFloatAsState(
        targetValue = if (showContent) 1f else 0.2f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "iconScale"
    )
    val iconAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(600), label = "iconAlpha"
    )
    val titleTranslationY by animateFloatAsState(
        targetValue = if (showContent) 0f else 60f,
        animationSpec = tween(700, 300, FastOutSlowInEasing), label = "titleY"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(700, 300), label = "titleAlpha"
    )
    val subtitleTranslationY by animateFloatAsState(
        targetValue = if (showContent) 0f else 40f,
        animationSpec = tween(700, 500, FastOutSlowInEasing), label = "subtitleY"
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(700, 500), label = "subtitleAlpha"
    )
    val taglineAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(700, 800), label = "taglineAlpha"
    )

    if (isFemale) {
        GirlSplash(
            iconScale, iconAlpha,
            titleTranslationY, titleAlpha,
            subtitleTranslationY, subtitleAlpha,
            taglineAlpha
        )
    } else {
        BoyDefaultSplash(
            iconScale, iconAlpha,
            titleTranslationY, titleAlpha,
            subtitleTranslationY, subtitleAlpha,
            taglineAlpha
        )
    }
}

// ── Girl / Pink splash ────────────────────────────────────────────────────────

@Composable
private fun GirlSplash(
    iconScale: Float, iconAlpha: Float,
    titleTranslationY: Float, titleAlpha: Float,
    subtitleTranslationY: Float, subtitleAlpha: Float,
    taglineAlpha: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E0914),
                        Color(0xFF2D0E1E),
                        Color(0xFF3C1228),
                        Color(0xFF4A1535)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height

            // Pink aurora / glowing orb at horizon
            val orbCenter = Offset(w * 0.5f, h * 0.68f)
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        Color(0x60EF5DA8),
                        Color(0x35C2185B),
                        Color(0x15901050),
                        Color.Transparent
                    ),
                    center = orbCenter,
                    radius = w * 0.60f
                ),
                radius = w * 0.60f,
                center = orbCenter
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x90FF80AB), Color(0x50EF5DA8), Color.Transparent),
                    center = orbCenter,
                    radius = w * 0.24f
                ),
                radius = w * 0.24f,
                center = orbCenter
            )

            // Far floral hills (smooth bezier bumps = meadow silhouette)
            val farHills = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.78f)
                cubicTo(w * 0.10f, h * 0.62f, w * 0.22f, h * 0.60f, w * 0.30f, h * 0.68f)
                cubicTo(w * 0.38f, h * 0.76f, w * 0.44f, h * 0.58f, w * 0.54f, h * 0.64f)
                cubicTo(w * 0.64f, h * 0.70f, w * 0.72f, h * 0.56f, w * 0.82f, h * 0.64f)
                cubicTo(w * 0.90f, h * 0.70f, w * 0.96f, h * 0.72f, w, h * 0.74f)
                lineTo(w, h)
                close()
            }
            drawPath(
                farHills,
                Brush.verticalGradient(
                    listOf(Color(0xFF3A1030), Color(0xFF0E0510)),
                    startY = h * 0.56f, endY = h
                )
            )

            // Near hills (foreground, darkest)
            val nearHills = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.88f)
                cubicTo(w * 0.08f, h * 0.82f, w * 0.20f, h * 0.79f, w * 0.30f, h * 0.84f)
                cubicTo(w * 0.40f, h * 0.89f, w * 0.50f, h * 0.80f, w * 0.60f, h * 0.84f)
                cubicTo(w * 0.72f, h * 0.88f, w * 0.84f, h * 0.80f, w * 0.92f, h * 0.84f)
                cubicTo(w * 0.96f, h * 0.86f, w, h * 0.85f, w, h * 0.86f)
                lineTo(w, h)
                close()
            }
            drawPath(nearHills, Color(0xFF080412))

            // Small flower/petal dots on hills horizon
            listOf(
                Offset(w * 0.18f, h * 0.67f), Offset(w * 0.46f, h * 0.63f),
                Offset(w * 0.72f, h * 0.62f), Offset(w * 0.88f, h * 0.69f),
                Offset(w * 0.32f, h * 0.70f)
            ).forEach { pos ->
                drawCircle(Color(0x50EF5DA8), 5f, pos)
                drawCircle(Color(0x30FFB3D1), 9f, pos)
            }

            // Pink sparkle stars
            listOf(
                Offset(w * 0.10f, h * 0.07f), Offset(w * 0.24f, h * 0.04f),
                Offset(w * 0.40f, h * 0.11f), Offset(w * 0.56f, h * 0.05f),
                Offset(w * 0.68f, h * 0.09f), Offset(w * 0.82f, h * 0.04f),
                Offset(w * 0.16f, h * 0.17f), Offset(w * 0.34f, h * 0.21f),
                Offset(w * 0.62f, h * 0.15f), Offset(w * 0.78f, h * 0.19f),
                Offset(w * 0.92f, h * 0.13f), Offset(w * 0.05f, h * 0.27f),
                Offset(w * 0.48f, h * 0.24f), Offset(w * 0.74f, h * 0.28f),
            ).forEach { pos ->
                drawCircle(Color(0x80FFB3D1), 2f, pos)
            }
            // Slightly bigger sparkles
            listOf(
                Offset(w * 0.28f, h * 0.10f),
                Offset(w * 0.86f, h * 0.22f),
                Offset(w * 0.52f, h * 0.16f)
            ).forEach { pos ->
                drawCircle(Color(0xA0EF5DA8), 3f, pos)
            }
        }

        // Centered content
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon with pink glow ring
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .graphicsLayer { scaleX = iconScale; scaleY = iconScale; alpha = iconAlpha },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(Color(0x20EF5DA8), size.minDimension * 0.50f)
                    drawCircle(Color(0x30EF5DA8), size.minDimension * 0.36f)
                    drawCircle(
                        Brush.radialGradient(listOf(Color(0xFF4A1040), Color(0xFF2D0A28))),
                        size.minDimension * 0.28f
                    )
                }
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = "MindMitra",
                    tint = Color(0xFFEF5DA8),
                    modifier = Modifier.size(58.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "MINDMITRA",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 5.sp,
                modifier = Modifier.graphicsLayer {
                    alpha = titleAlpha; translationY = titleTranslationY
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Your AI Companion for Mental Wellness",
                fontSize = 14.sp,
                color = Color(0xFFFFB3D1),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.graphicsLayer {
                    alpha = subtitleAlpha; translationY = subtitleTranslationY
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "💗  Feel. Heal. Glow.",
                fontSize = 13.sp,
                color = Color(0xFFEF5DA8),
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.5.sp,
                modifier = Modifier.graphicsLayer { alpha = taglineAlpha }
            )
        }
    }
}

// ── Boy / Default dark splash ─────────────────────────────────────────────────

@Composable
private fun BoyDefaultSplash(
    iconScale: Float, iconAlpha: Float,
    titleTranslationY: Float, titleAlpha: Float,
    subtitleTranslationY: Float, subtitleAlpha: Float,
    taglineAlpha: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF080618),
                        Color(0xFF0D0B28),
                        Color(0xFF130E3A),
                        Color(0xFF180F48)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height

            val orbCenter = Offset(w * 0.5f, h * 0.70f)
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x50A090FF), Color(0x30705ADB), Color(0x15503CB5), Color.Transparent),
                    center = orbCenter, radius = w * 0.55f
                ),
                radius = w * 0.55f, center = orbCenter
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x80C8AAFF), Color(0x50907ADB), Color.Transparent),
                    center = orbCenter, radius = w * 0.22f
                ),
                radius = w * 0.22f, center = orbCenter
            )

            val farMountains = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.76f)
                lineTo(w * 0.08f, h * 0.64f); lineTo(w * 0.18f, h * 0.72f)
                lineTo(w * 0.28f, h * 0.57f); lineTo(w * 0.38f, h * 0.68f)
                lineTo(w * 0.48f, h * 0.54f); lineTo(w * 0.58f, h * 0.66f)
                lineTo(w * 0.68f, h * 0.53f); lineTo(w * 0.80f, h * 0.67f)
                lineTo(w * 0.90f, h * 0.56f); lineTo(w, h * 0.70f)
                lineTo(w, h); close()
            }
            drawPath(
                farMountains,
                Brush.verticalGradient(
                    listOf(Color(0xFF1A1040), Color(0xFF0A0820)),
                    startY = h * 0.5f, endY = h
                )
            )

            val nearHills = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.87f)
                lineTo(w * 0.12f, h * 0.81f); lineTo(w * 0.25f, h * 0.86f)
                lineTo(w * 0.38f, h * 0.78f); lineTo(w * 0.52f, h * 0.84f)
                lineTo(w * 0.65f, h * 0.77f); lineTo(w * 0.78f, h * 0.82f)
                lineTo(w * 0.90f, h * 0.79f); lineTo(w, h * 0.83f)
                lineTo(w, h); close()
            }
            drawPath(nearHills, Color(0xFF060512))

            val starPositions = listOf(
                Offset(w * 0.10f, h * 0.08f), Offset(w * 0.25f, h * 0.04f),
                Offset(w * 0.40f, h * 0.12f), Offset(w * 0.55f, h * 0.06f),
                Offset(w * 0.70f, h * 0.10f), Offset(w * 0.85f, h * 0.05f),
                Offset(w * 0.15f, h * 0.18f), Offset(w * 0.32f, h * 0.22f),
                Offset(w * 0.60f, h * 0.16f), Offset(w * 0.78f, h * 0.20f),
                Offset(w * 0.92f, h * 0.14f), Offset(w * 0.05f, h * 0.28f),
                Offset(w * 0.45f, h * 0.25f), Offset(w * 0.72f, h * 0.30f),
            )
            starPositions.forEach { pos ->
                drawCircle(Color(0x80FFFFFF), radius = 2f, center = pos)
            }
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .graphicsLayer { scaleX = iconScale; scaleY = iconScale; alpha = iconAlpha },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(Color(0x206C5CE7), size.minDimension * 0.50f)
                    drawCircle(Color(0x306C5CE7), size.minDimension * 0.36f)
                    drawCircle(
                        Brush.radialGradient(listOf(Color(0xFF3A2F7A), Color(0xFF22196A))),
                        size.minDimension * 0.28f
                    )
                }
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = "MindMitra",
                    tint = AccentLavender,
                    modifier = Modifier.size(58.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "MINDMITRA",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 5.sp,
                modifier = Modifier.graphicsLayer {
                    alpha = titleAlpha; translationY = titleTranslationY
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Your AI Companion for Mental Wellness",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.graphicsLayer {
                    alpha = subtitleAlpha; translationY = subtitleTranslationY
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "♥  Feel. Heal. Grow.",
                fontSize = 13.sp,
                color = AccentLavender,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.5.sp,
                modifier = Modifier.graphicsLayer { alpha = taglineAlpha }
            )
        }
    }
}
