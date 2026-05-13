package com.mindmitra.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mindmitra.app.navigation.Routes
import com.mindmitra.app.ui.theme.GirlBg
import com.mindmitra.app.ui.theme.GirlBorder
import com.mindmitra.app.ui.theme.GirlCard
import com.mindmitra.app.ui.theme.GirlNavBg
import com.mindmitra.app.ui.theme.GirlPrimary
import com.mindmitra.app.ui.theme.GirlPrimaryDim
import com.mindmitra.app.ui.theme.GirlTextDark
import com.mindmitra.app.ui.theme.GirlTextLight
import com.mindmitra.app.ui.theme.GirlTextMid
import com.mindmitra.app.ui.viewmodel.AuthViewModel
import com.mindmitra.app.ui.viewmodel.UserViewModel
import java.util.Calendar

@Composable
fun GirlHomeScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    // Force dark status bar icons — light background
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            androidx.core.view.WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = true
        }
    }

    LaunchedEffect(Unit) { userViewModel.checkAndUpdateStreak() }

    Scaffold(
        bottomBar = { GirlBottomBar(navController = navController, selectedIndex = 0) },
        containerColor = GirlBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(GirlBg),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item { GirlTopBar(navController) }
            item { GirlGreetingSection(userName = userViewModel.userName) }
            item { Spacer(modifier = Modifier.height(20.dp)) }
            item { GirlMoodCheckInCard(onTrackMood = { navController.navigate(Routes.GIRL_MOOD_TRACKER) }) }
            item { Spacer(modifier = Modifier.height(14.dp)) }
            item { GirlStreakCard(streakCount = userViewModel.streakCount, onClick = { navController.navigate(Routes.STREAK_MAP) }) }
            item { Spacer(modifier = Modifier.height(22.dp)) }
            item { GirlQuickActionsSection(navController) }
            item { Spacer(modifier = Modifier.height(22.dp)) }
            item { GirlTodayForYouSection(navController) }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun GirlTopBar(navController: NavController) {
    val authViewModel: AuthViewModel = viewModel()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "MindMitra ✨",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = GirlPrimary
        )
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(GirlCard, CircleShape)
                .border(1.dp, GirlBorder, CircleShape)
                .clickable {
                    authViewModel.logout()
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.GIRL_HOME) { inclusive = true }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Logout",
                tint = GirlPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun GirlGreetingSection(userName: String) {
    val calendar = remember { Calendar.getInstance() }
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..20 -> "Good Evening"
        else -> "Good Night"
    }
    val emoji = when (hour) {
        in 5..11 -> "🌸"
        in 12..16 -> "🌺"
        in 17..20 -> "🌙"
        else -> "💫"
    }
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "$greeting, $userName $emoji",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = GirlTextDark
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "You're not alone. I'm here with you. 💗", fontSize = 14.sp, color = GirlTextMid)
    }
}

@Composable
private fun GirlMoodCheckInCard(onTrackMood: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GirlPrimaryDim),
        border = androidx.compose.foundation.BorderStroke(1.dp, GirlBorder)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "How are you feeling today?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GirlTextDark,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Track your mood and understand yourself better",
                    fontSize = 12.sp,
                    color = GirlTextMid,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onTrackMood,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GirlPrimary),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.height(38.dp),
                ) {
                    Text(text = "Track Mood", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Pink girl mascot
            Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(colors = listOf(Color(0x40EF5DA8), Color.Transparent)),
                        radius = size.minDimension * 0.55f
                    )
                    drawOval(
                        brush = Brush.radialGradient(colors = listOf(Color(0xFFFF80AB), Color(0xFFEF5DA8))),
                        topLeft = Offset(size.width * 0.08f, size.height * 0.12f),
                        size = Size(size.width * 0.84f, size.height * 0.76f)
                    )
                    drawCircle(color = Color.White, radius = 6f, center = Offset(size.width * 0.36f, size.height * 0.40f))
                    drawCircle(color = Color.White, radius = 6f, center = Offset(size.width * 0.64f, size.height * 0.40f))
                    drawCircle(color = Color(0xFF880E4F), radius = 3f, center = Offset(size.width * 0.36f, size.height * 0.42f))
                    drawCircle(color = Color(0xFF880E4F), radius = 3f, center = Offset(size.width * 0.64f, size.height * 0.42f))
                    drawArc(
                        color = Color(0xFF880E4F),
                        startAngle = 20f, sweepAngle = 140f, useCenter = false,
                        topLeft = Offset(size.width * 0.30f, size.height * 0.44f),
                        size = Size(size.width * 0.40f, size.height * 0.24f),
                        style = Stroke(width = 3f, cap = StrokeCap.Round)
                    )
                    // Blush cheeks
                    drawCircle(Color(0xFFFFCDD2).copy(0.6f), 8f, Offset(size.width * 0.25f, size.height * 0.50f))
                    drawCircle(Color(0xFFFFCDD2).copy(0.6f), 8f, Offset(size.width * 0.75f, size.height * 0.50f))
                    // Sparkle
                    drawCircle(color = Color(0xFFFFD700), radius = 3f, center = Offset(size.width * 0.82f, size.height * 0.14f))
                }
            }
        }
    }
}

@Composable
private fun GirlStreakCard(streakCount: Int = 0, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GirlCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GirlBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Daily Check-in Streak ", fontSize = 13.sp, color = GirlTextMid)
                    Text(text = "🌸", fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = "$streakCount", fontSize = 44.sp, fontWeight = FontWeight.Bold, color = GirlPrimary, lineHeight = 44.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Days", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = GirlTextMid, modifier = Modifier.padding(bottom = 6.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Keep going, You're doing amazing! 💕", fontSize = 12.sp, color = GirlTextMid)
            }
            Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val sw = 7.dp.toPx()
                    val r = (size.minDimension - sw) / 2f
                    val c = Offset(size.width / 2f, size.height / 2f)
                    drawCircle(color = Color(0xFFFFE4EF), radius = r, center = c, style = Stroke(width = sw))
                    drawArc(
                        brush = Brush.sweepGradient(colors = listOf(Color(0xFFEF5DA8), Color(0xFFFFB3D1), Color(0xFFEF5DA8)), center = c),
                        startAngle = -90f, sweepAngle = (streakCount.coerceAtMost(30) / 30f * 360f).coerceAtLeast(4f), useCenter = false,
                        style = Stroke(width = sw, cap = StrokeCap.Round)
                    )
                }
                Text(text = "🌸", fontSize = 24.sp)
            }
        }
    }
}

@Composable
private fun GirlQuickActionsSection(navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Quick Actions", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = GirlTextDark)
            Text(text = "View All  ›", fontSize = 13.sp, color = GirlPrimary, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            GirlQuickActionCard("AI Chat",   "Talk & Feel Better",  { AIChatActionIcon()    }) { navController.navigate(Routes.GIRL_CHAT)      }
            GirlQuickActionCard("Breathing", "Calm Your Mind",      { BreathingActionIcon() }) { navController.navigate(Routes.GIRL_BREATHING)  }
            GirlQuickActionCard("Journal",   "Write Your Thoughts", { JournalActionIcon()   }) { navController.navigate(Routes.GIRL_JOURNAL)    }
            GirlQuickActionCard("Quotes",    "Daily Motivation",    { QuotesActionIcon()    }) { navController.navigate(Routes.GIRL_COMMUNITY)  }
        }
    }
}

@Composable
private fun GirlQuickActionCard(
    label: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.width(76.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F7)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD6E7))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(52.dp), contentAlignment = Alignment.Center) {
                icon()
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GirlTextDark,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 9.5.sp,
                color = GirlTextMid,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}

// ── Custom illustrated icons ──────────────────────────────────────────

private fun DrawScope.drawSparkle(center: Offset, size: Float, color: Color) {
    val path = Path().apply {
        moveTo(center.x, center.y - size)
        lineTo(center.x + size * 0.25f, center.y - size * 0.25f)
        lineTo(center.x + size, center.y)
        lineTo(center.x + size * 0.25f, center.y + size * 0.25f)
        lineTo(center.x, center.y + size)
        lineTo(center.x - size * 0.25f, center.y + size * 0.25f)
        lineTo(center.x - size, center.y)
        lineTo(center.x - size * 0.25f, center.y - size * 0.25f)
        close()
    }
    drawPath(path, color)
}

private fun heartPath(center: Offset, size: Float): Path = Path().apply {
    val s = size / 2f
    moveTo(center.x, center.y + s)
    cubicTo(center.x - s * 2.2f, center.y, center.x - s * 2.2f, center.y - s * 1.5f, center.x, center.y - s * 0.5f)
    cubicTo(center.x + s * 2.2f, center.y - s * 1.5f, center.x + s * 2.2f, center.y, center.x, center.y + s)
    close()
}

@Composable
private fun AIChatActionIcon() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0x30EF5DA8), Color.Transparent)),
            radius = w * 0.50f
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFFFF80AB), Color(0xFFEF5DA8)),
                center = Offset(w * 0.45f, h * 0.40f)
            ),
            radius = w * 0.35f,
            center = Offset(w * 0.50f, h * 0.42f)
        )
        val tail = Path().apply {
            moveTo(w * 0.24f, h * 0.70f)
            lineTo(w * 0.14f, h * 0.86f)
            lineTo(w * 0.42f, h * 0.70f)
            close()
        }
        drawPath(tail, Color(0xFFEF5DA8))
        drawPath(heartPath(Offset(w * 0.50f, h * 0.44f), w * 0.26f), Color.White)
        drawPath(heartPath(Offset(w * 0.79f, h * 0.16f), w * 0.10f), Color(0xFFFFB3D1))
        drawSparkle(Offset(w * 0.11f, h * 0.12f), 4f, Color(0xFFEF5DA8))
        drawSparkle(Offset(w * 0.88f, h * 0.30f), 3f, Color(0xFFFFB3D1))
        drawSparkle(Offset(w * 0.80f, h * 0.84f), 3f, Color(0xFFFF80AB))
    }
}

@Composable
private fun BreathingActionIcon() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0x20EF5DA8), Color.Transparent)),
            radius = w * 0.50f
        )
        val stroke = Stroke(width = 3.5f.dp.toPx(), cap = StrokeCap.Round)
        val pinkBrush = Brush.linearGradient(listOf(Color(0xFFFF80AB), Color(0xFFEF5DA8)))
        val path1 = Path().apply {
            moveTo(w * 0.10f, h * 0.32f)
            cubicTo(w * 0.30f, h * 0.20f, w * 0.55f, h * 0.30f, w * 0.70f, h * 0.28f)
            cubicTo(w * 0.82f, h * 0.26f, w * 0.88f, h * 0.34f, w * 0.80f, h * 0.40f)
            cubicTo(w * 0.72f, h * 0.46f, w * 0.60f, h * 0.42f, w * 0.55f, h * 0.38f)
        }
        drawPath(path1, pinkBrush, style = stroke)
        val path2 = Path().apply {
            moveTo(w * 0.08f, h * 0.50f)
            cubicTo(w * 0.25f, h * 0.40f, w * 0.55f, h * 0.52f, w * 0.75f, h * 0.50f)
            cubicTo(w * 0.88f, h * 0.48f, w * 0.92f, h * 0.56f, w * 0.84f, h * 0.62f)
            cubicTo(w * 0.76f, h * 0.68f, w * 0.64f, h * 0.64f, w * 0.58f, h * 0.60f)
        }
        drawPath(path2, pinkBrush, style = stroke)
        val path3 = Path().apply {
            moveTo(w * 0.10f, h * 0.68f)
            cubicTo(w * 0.28f, h * 0.60f, w * 0.52f, h * 0.70f, w * 0.66f, h * 0.68f)
            cubicTo(w * 0.76f, h * 0.66f, w * 0.80f, h * 0.72f, w * 0.74f, h * 0.76f)
        }
        drawPath(path3, pinkBrush, style = stroke)
        drawSparkle(Offset(w * 0.88f, h * 0.12f), 4f, Color(0xFFEF5DA8))
        drawSparkle(Offset(w * 0.10f, h * 0.86f), 3f, Color(0xFFFFB3D1))
        drawSparkle(Offset(w * 0.84f, h * 0.90f), 3f, Color(0xFFFF80AB))
    }
}

@Composable
private fun JournalActionIcon() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0x25EF5DA8), Color.Transparent)),
            radius = w * 0.50f
        )
        drawRoundRect(
            brush = Brush.linearGradient(listOf(Color(0xFFFF4D8D), Color(0xFFEF5DA8))),
            topLeft = Offset(w * 0.22f, h * 0.12f),
            size = Size(w * 0.62f, h * 0.76f),
            cornerRadius = CornerRadius(8.dp.toPx())
        )
        drawRoundRect(
            color = Color(0xFFD81B60),
            topLeft = Offset(w * 0.22f, h * 0.12f),
            size = Size(w * 0.10f, h * 0.76f),
            cornerRadius = CornerRadius(8.dp.toPx())
        )
        val lineColor = Color(0x30FFFFFF)
        for (i in 0..2) {
            val lineY = h * (0.56f + i * 0.10f)
            drawLine(lineColor, Offset(w * 0.38f, lineY), Offset(w * 0.78f, lineY), strokeWidth = 2f)
        }
        drawPath(heartPath(Offset(w * 0.52f, h * 0.37f), w * 0.24f), Color.White)
        drawPath(heartPath(Offset(w * 0.79f, h * 0.14f), w * 0.08f), Color(0xFFFFB3D1))
        drawPath(heartPath(Offset(w * 0.84f, h * 0.82f), w * 0.07f), Color(0xFFFF80AB))
        drawSparkle(Offset(w * 0.10f, h * 0.14f), 4f, Color(0xFFEF5DA8))
        drawSparkle(Offset(w * 0.88f, h * 0.38f), 3f, Color(0xFFFFB3D1))
    }
}

@Composable
private fun QuotesActionIcon() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        val cx = w * 0.50f; val cy = h * 0.54f
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0x25EF5DA8), Color.Transparent)),
            radius = w * 0.50f
        )
        for (i in 0..4) {
            withTransform({ rotate(i * 72f, Offset(cx, cy)) }) {
                drawOval(
                    brush = Brush.radialGradient(listOf(Color(0xFFFFB3D1), Color(0xFFFF80AB))),
                    topLeft = Offset(cx - w * 0.09f, cy - h * 0.40f),
                    size = Size(w * 0.18f, h * 0.24f)
                )
            }
        }
        for (i in 0..4) {
            withTransform({ rotate(i * 72f + 36f, Offset(cx, cy)) }) {
                drawOval(
                    brush = Brush.radialGradient(listOf(Color(0xFFFF80AB), Color(0xFFEF5DA8))),
                    topLeft = Offset(cx - w * 0.07f, cy - h * 0.30f),
                    size = Size(w * 0.14f, h * 0.18f)
                )
            }
        }
        drawCircle(Color(0xFFFF4D8D), w * 0.10f, Offset(cx, cy))
        drawSparkle(Offset(w * 0.12f, h * 0.10f), 4f, Color(0xFFEF5DA8))
        drawSparkle(Offset(w * 0.86f, h * 0.12f), 3f, Color(0xFFFFB3D1))
        drawSparkle(Offset(w * 0.88f, h * 0.82f), 3f, Color(0xFFFF80AB))
    }
}

@Composable
private fun GirlTodayForYouSection(navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(text = "Today for you", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = GirlTextDark)
        Spacer(modifier = Modifier.height(12.dp))
        GirlTodayCard(
            emoji = "🌬️",
            iconBg = Color(0xFFE8F5E9),
            title = "Take a deep breath",
            subtitle = "You've got this! 💕",
            meta = "3 min  •  Breathing Exercise",
            onClick = { navController.navigate(Routes.GIRL_BREATHING) }
        )
        Spacer(modifier = Modifier.height(10.dp))
        GirlTodayCard(
            emoji = "✨",
            iconBg = GirlPrimaryDim,
            title = "Daily motivation",
            subtitle = "\"Small steps lead to big changes.\"",
            meta = "Tap to get today's quote"
        )
    }
}

@Composable
private fun GirlTodayCard(emoji: String, iconBg: Color, title: String, subtitle: String, meta: String, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GirlCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GirlBorder)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(46.dp).background(iconBg, CircleShape), contentAlignment = Alignment.Center) {
                Text(text = emoji, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = GirlTextDark)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 12.sp, color = GirlTextMid)
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = meta, fontSize = 11.sp, color = GirlTextLight)
            }
            Box(
                modifier = Modifier.size(40.dp).background(GirlPrimary, CircleShape).clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start", tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
fun GirlBottomBar(navController: NavController, selectedIndex: Int) {
    val items = listOf(
        Triple("Home",      Icons.Default.Home,     Routes.GIRL_HOME),
        Triple("Chat",      Icons.Default.Chat,     Routes.GIRL_CHAT),
        Triple("Journal",   Icons.Default.Book,     Routes.GIRL_JOURNAL),
        Triple("Com",       Icons.Default.Group,    Routes.GIRL_COMMUNITY),
        Triple("Profile",   Icons.Default.Person,   Routes.GIRL_PROFILE),
    )
    NavigationBar(containerColor = GirlNavBg, tonalElevation = 0.dp) {
        items.forEachIndexed { index, (label, icon, route) ->
            val isSelected = index == selectedIndex
            NavigationBarItem(
                icon = { Icon(icon, label, modifier = Modifier.size(22.dp)) },
                label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal) },
                selected = isSelected,
                onClick = {
                    if (!isSelected) navController.navigate(route) {
                        popUpTo(Routes.GIRL_HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GirlPrimary,
                    selectedTextColor = GirlPrimary,
                    unselectedIconColor = GirlTextLight,
                    unselectedTextColor = GirlTextLight,
                    indicatorColor = GirlPrimaryDim
                )
            )
        }
    }
}
