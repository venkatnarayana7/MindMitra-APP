package com.mindmitra.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mindmitra.app.ui.theme.TextHint
import com.mindmitra.app.ui.theme.TextPrimary
import com.mindmitra.app.ui.theme.TextSecondary
import com.mindmitra.app.ui.viewmodel.UserViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

private val PHASE_NAMES  = listOf("Inhale", "Hold", "Exhale", "Hold")
private val PHASE_COLORS = listOf(
    Color(0xFF5B9BFF), // Inhale  — bright blue
    Color(0xFFA78BFA), // Hold    — lavender
    Color(0xFF4ECDC4), // Exhale  — teal
    Color(0xFFA78BFA), // Hold    — lavender
)

@Composable
fun BreathingScreen(navController: NavController, userViewModel: UserViewModel) {
    val sweep            = remember { Animatable(0f) }
    var isPlaying        by remember { mutableStateOf(true) }
    var phaseIndex       by remember { mutableIntStateOf(0) }
    var countdown        by remember { mutableIntStateOf(4) }

    val phaseColor = PHASE_COLORS[phaseIndex]
    val phaseName  = PHASE_NAMES[phaseIndex]

    // Main animation loop — restarts whenever isPlaying toggles
    LaunchedEffect(isPlaying) {
        if (!isPlaying) {
            sweep.stop()
            return@LaunchedEffect
        }
        sweep.snapTo(0f)
        countdown = 4
        while (isActive) {
            coroutineScope {
                launch {
                    sweep.animateTo(
                        targetValue = 360f,
                        animationSpec = tween(durationMillis = 4000, easing = LinearEasing)
                    )
                }
                for (i in 4 downTo 1) {
                    countdown = i
                    delay(1000L)
                }
            }
            val nextPhase = (phaseIndex + 1) % 4
            if (nextPhase == 0) {
                userViewModel.incrementBreathingSession()
            }
            phaseIndex = nextPhase
            sweep.snapTo(0f)
            countdown = 4
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080820))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Top bar ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFF12122E), CircleShape)
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Box Breathing",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "4-4-4-4 Technique",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFF12122E), CircleShape)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // ── Breathing circle ──────────────────────────────────────────────
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val strokeWidth = 14.dp.toPx()
                    val trackR = size.minDimension / 2f - strokeWidth / 2f

                    // Outer soft ambient glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                phaseColor.copy(alpha = 0.18f),
                                phaseColor.copy(alpha = 0.06f),
                                Color.Transparent
                            ),
                            center = Offset(cx, cy),
                            radius = trackR * 1.5f
                        ),
                        radius = trackR * 1.5f,
                        center = Offset(cx, cy)
                    )

                    // Inner glow ring
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                phaseColor.copy(alpha = 0.10f),
                                Color.Transparent
                            ),
                            center = Offset(cx, cy),
                            radius = trackR
                        ),
                        radius = trackR,
                        center = Offset(cx, cy)
                    )

                    // Track ring
                    drawCircle(
                        color = Color.White.copy(alpha = 0.07f),
                        radius = trackR,
                        center = Offset(cx, cy),
                        style = Stroke(width = strokeWidth)
                    )

                    // Progress arc
                    val sweepVal = sweep.value
                    if (sweepVal > 0f) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    phaseColor.copy(alpha = 0.15f),
                                    phaseColor.copy(alpha = 0.6f),
                                    phaseColor,
                                    phaseColor
                                ),
                                center = Offset(cx, cy)
                            ),
                            startAngle = -90f,
                            sweepAngle = sweepVal,
                            useCenter = false,
                            topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                            size = Size(size.width - strokeWidth, size.height - strokeWidth),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Glowing tip dot at arc head
                        val tipRad = Math.toRadians((-90.0 + sweepVal))
                        val tipX = cx + trackR * cos(tipRad).toFloat()
                        val tipY = cy + trackR * sin(tipRad).toFloat()
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.95f),
                                    phaseColor.copy(alpha = 0.7f),
                                    Color.Transparent
                                ),
                                center = Offset(tipX, tipY),
                                radius = strokeWidth * 1.2f
                            ),
                            radius = strokeWidth * 0.9f,
                            center = Offset(tipX, tipY)
                        )
                    }
                }

                // Centre text
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = phaseName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = phaseColor
                    )
                    Text(
                        text = countdown.toString(),
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        lineHeight = 76.sp
                    )
                    Text(
                        text = "Seconds",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Bottom section ────────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                // Phase sequence row
                PhaseSequenceRow(activeIndex = phaseIndex)

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Helps reduce stress and anxiety",
                    fontSize = 13.sp,
                    color = TextHint
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Play / pause button
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF6C5CE7), Color(0xFF4A3AAA))
                            ),
                            shape = CircleShape
                        )
                        .clickable { isPlaying = !isPlaying },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

// ─── Phase sequence pills ─────────────────────────────────────────────────────

@Composable
private fun PhaseSequenceRow(activeIndex: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        PHASE_NAMES.forEachIndexed { i, name ->
            val isActive = i == activeIndex
            val color    = PHASE_COLORS[i]

            Box(
                modifier = Modifier
                    .background(
                        color = if (isActive) color.copy(alpha = 0.2f) else Color(0xFF12122E),
                        shape = CircleShape
                    )
                    .then(
                        if (isActive) Modifier else Modifier
                    )
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = name,
                    fontSize = 12.sp,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isActive) color else TextHint
                )
            }

            if (i < PHASE_NAMES.size - 1) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "→", fontSize = 11.sp, color = TextHint)
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}
