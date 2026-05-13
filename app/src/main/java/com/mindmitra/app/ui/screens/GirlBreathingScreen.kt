package com.mindmitra.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mindmitra.app.ui.theme.GirlBg
import com.mindmitra.app.ui.theme.GirlBorder
import com.mindmitra.app.ui.theme.GirlCard
import com.mindmitra.app.ui.theme.GirlPrimary
import com.mindmitra.app.ui.theme.GirlPrimaryDim
import com.mindmitra.app.ui.theme.GirlPrimaryLight
import com.mindmitra.app.ui.theme.GirlTextDark
import com.mindmitra.app.ui.theme.GirlTextMid
import com.mindmitra.app.ui.viewmodel.UserViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

private data class GirlBreathPhase(val label: String, val color: Color, val durationMs: Int)

@Composable
fun GirlBreathingScreen(navController: NavController, userViewModel: UserViewModel) {
    val phases = listOf(
        GirlBreathPhase("Inhale",   Color(0xFFEF5DA8), 4000),
        GirlBreathPhase("Hold",     Color(0xFF9C27B0), 4000),
        GirlBreathPhase("Exhale",   Color(0xFF4CAF50), 4000),
        GirlBreathPhase("Rest",     Color(0xFF2196F3), 4000),
    )

    var isPlaying by remember { mutableStateOf(false) }
    var phaseIndex by remember { mutableIntStateOf(0) }
    var countdown by remember { mutableIntStateOf(4) }
    val sweep = remember { Animatable(0f) }

    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        while (true) {
            val phase = phases[phaseIndex % phases.size]
            sweep.snapTo(0f)
            coroutineScope {
                launch { sweep.animateTo(360f, tween(phase.durationMs, easing = LinearEasing)) }
                for (i in 4 downTo 1) {
                    countdown = i
                    delay(1000L)
                }
            }
            val nextPhase = (phaseIndex + 1) % phases.size
            if (nextPhase == 0) {
                userViewModel.incrementBreathingSession()
            }
            phaseIndex = nextPhase
        }
    }

    val currentPhase = phases[phaseIndex % phases.size]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GirlBg)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp).background(GirlCard, CircleShape).border(1.dp, GirlBorder, CircleShape).clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, "Back", tint = GirlPrimary, modifier = Modifier.size(18.dp))
            }
        }

        Text("Box Breathing 🌸", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GirlTextDark)
        Spacer(modifier = Modifier.height(6.dp))
        Text("4-4-4-4 breathing to calm your mind 💗", fontSize = 14.sp, color = GirlTextMid, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))

        Spacer(modifier = Modifier.height(40.dp))

        // Breathing ring
        Box(modifier = Modifier.size(240.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 14.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2f
                val center = Offset(size.width / 2f, size.height / 2f)

                // Glow
                drawCircle(brush = Brush.radialGradient(listOf(currentPhase.color.copy(0.15f), Color.Transparent), center = center, radius = radius * 1.3f), radius = radius * 1.3f, center = center)
                // Track
                drawCircle(color = GirlPrimaryDim, radius = radius, center = center, style = Stroke(width = strokeWidth))
                // Arc
                drawArc(
                    brush = Brush.sweepGradient(listOf(currentPhase.color.copy(0.5f), currentPhase.color, currentPhase.color), center = center),
                    startAngle = -90f,
                    sweepAngle = sweep.value,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Tip dot
                if (sweep.value > 0f) {
                    val tipAngle = Math.toRadians((sweep.value - 90f).toDouble())
                    val tipX = center.x + radius * cos(tipAngle).toFloat()
                    val tipY = center.y + radius * sin(tipAngle).toFloat()
                    drawCircle(color = Color.White, radius = 10.dp.toPx(), center = Offset(tipX, tipY))
                    drawCircle(color = currentPhase.color, radius = 7.dp.toPx(), center = Offset(tipX, tipY))
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(currentPhase.label, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = currentPhase.color)
                Spacer(modifier = Modifier.height(4.dp))
                Text("$countdown", fontSize = 42.sp, fontWeight = FontWeight.Bold, color = GirlTextDark, lineHeight = 42.sp)
                Text("seconds", fontSize = 12.sp, color = GirlTextMid)
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Phase indicators
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            phases.forEachIndexed { index, phase ->
                val isActive = (phaseIndex % phases.size) == index
                Box(
                    modifier = Modifier
                        .background(if (isActive) phase.color.copy(0.15f) else GirlCard, RoundedCornerShape(10.dp))
                        .border(if (isActive) 2.dp else 1.dp, if (isActive) phase.color else GirlBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(phase.label, fontSize = 12.sp, fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal, color = if (isActive) phase.color else GirlTextMid)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Play/Pause button
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Brush.radialGradient(listOf(GirlPrimaryLight, GirlPrimary)), CircleShape)
                .clickable { isPlaying = !isPlaying },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(if (isPlaying) "Tap to pause" else "Tap to begin", fontSize = 13.sp, color = GirlTextMid)

        Spacer(modifier = Modifier.height(32.dp))

        // Tip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(GirlPrimaryDim, RoundedCornerShape(16.dp))
                .border(1.dp, GirlBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "💡 Breathe in through your nose, hold gently, then exhale slowly through your mouth. You've got this! 💗",
                fontSize = 13.sp,
                color = GirlTextDark,
                lineHeight = 19.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
