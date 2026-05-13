package com.mindmitra.app.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mindmitra.app.navigation.Routes
import com.mindmitra.app.ui.viewmodel.AuthViewModel
import com.mindmitra.app.ui.viewmodel.UserViewModel
import com.mindmitra.app.ui.theme.AccentLavender
import com.mindmitra.app.ui.theme.BlueAccent
import com.mindmitra.app.ui.theme.BottomNavBg
import com.mindmitra.app.ui.theme.CardSurface
import com.mindmitra.app.ui.theme.DeepNavy
import com.mindmitra.app.ui.theme.GreenTeal
import com.mindmitra.app.ui.theme.MoodCardBg
import com.mindmitra.app.ui.theme.OrangeAccent
import com.mindmitra.app.ui.theme.PrimaryPurple
import com.mindmitra.app.ui.theme.StreakCardBg
import com.mindmitra.app.ui.theme.StreakOrange
import com.mindmitra.app.ui.theme.TextHint
import com.mindmitra.app.ui.theme.TextPrimary
import com.mindmitra.app.ui.theme.TextSecondary
import java.util.Calendar

@Composable
fun HomeScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    // Force light (white) status bar icons — dark background
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            androidx.core.view.WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = false
        }
    }

    // Streak: run once when home screen appears
    LaunchedEffect(Unit) { userViewModel.checkAndUpdateStreak() }

    Scaffold(
        bottomBar = { MindMitraBottomBar(navController = navController) },
        containerColor = DeepNavy
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DeepNavy),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item { TopBar(navController = navController) }
            item { GreetingSection(userName = userViewModel.userName) }
            item { Spacer(modifier = Modifier.height(20.dp)) }
            item { MoodCheckInCard(onTrackMood = { navController.navigate(Routes.MOOD_TRACKER) }) }
            item { Spacer(modifier = Modifier.height(14.dp)) }
            item { StreakCard(streakCount = userViewModel.streakCount, onClick = { navController.navigate(Routes.STREAK_MAP) }) }
            item { Spacer(modifier = Modifier.height(22.dp)) }
            item { QuickActionsSection(navController) }
            item { Spacer(modifier = Modifier.height(22.dp)) }
            item { TodayForYouSection(navController) }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun TopBar(navController: NavController) {
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
            color = AccentLavender
        )
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(CardSurface, CircleShape)
                .border(1.dp, PrimaryPurple.copy(alpha = 0.35f), CircleShape)
                .clickable {
                    authViewModel.logout()
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Logout",
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun GreetingSection(userName: String) {
    val calendar = remember { Calendar.getInstance() }
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    val greeting = when (hour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..20 -> "Good Evening"
        else -> "Good Night"
    }
    val emoji = when (hour) {
        in 5..11 -> "☀️"
        in 12..16 -> "🌤️"
        in 17..20 -> "🌙"
        else -> "⭐"
    }

    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Text(
            text = "$greeting, $userName $emoji",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "You're not alone. I'm here with you.",
            fontSize = 14.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun MoodCheckInCard(onTrackMood: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MoodCardBg),
        border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.25f))
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
                    color = TextPrimary,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Track your mood and understand yourself better",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onTrackMood,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text(
                        text = "Track Mood",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Mascot blob character (drawn with Canvas)
            Box(
                modifier = Modifier.size(84.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Glow behind mascot
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x40685DC8), Color.Transparent)
                        ),
                        radius = size.minDimension * 0.55f
                    )
                    // Mascot body (rounded blob)
                    drawOval(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF5A52A8), Color(0xFF3A3480))
                        ),
                        topLeft = Offset(size.width * 0.08f, size.height * 0.12f),
                        size = Size(size.width * 0.84f, size.height * 0.76f)
                    )
                    // Eyes
                    drawCircle(color = Color.White, radius = 6f, center = Offset(size.width * 0.36f, size.height * 0.40f))
                    drawCircle(color = Color.White, radius = 6f, center = Offset(size.width * 0.64f, size.height * 0.40f))
                    drawCircle(color = Color(0xFF2A2060), radius = 3.5f, center = Offset(size.width * 0.36f, size.height * 0.42f))
                    drawCircle(color = Color(0xFF2A2060), radius = 3.5f, center = Offset(size.width * 0.64f, size.height * 0.42f))
                    // Smile
                    drawArc(
                        color = Color.White,
                        startAngle = 20f,
                        sweepAngle = 140f,
                        useCenter = false,
                        topLeft = Offset(size.width * 0.30f, size.height * 0.44f),
                        size = Size(size.width * 0.40f, size.height * 0.24f),
                        style = Stroke(width = 3f, cap = StrokeCap.Round)
                    )
                    // Star sparkle (top-right of mascot)
                    drawCircle(
                        color = Color(0xFFFFD700),
                        radius = 3f,
                        center = Offset(size.width * 0.82f, size.height * 0.14f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StreakCard(streakCount: Int = 0, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = StreakCardBg),
        border = BorderStroke(1.dp, StreakOrange.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Daily Check-in Streak ", fontSize = 13.sp, color = TextSecondary)
                    Text(text = "🔥", fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$streakCount",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        lineHeight = 44.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Days",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Keep going, You're doing great!",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            // Circular progress ring with flame
            Box(
                modifier = Modifier.size(72.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 7.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)

                    // Track
                    drawCircle(
                        color = Color(0xFF2A2850),
                        radius = radius,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )
                    // Progress (7/30 days = ~23%)
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(StreakOrange, Color(0xFFFFAA44), StreakOrange),
                            center = center
                        ),
                        startAngle = -90f,
                        sweepAngle = (streakCount.coerceAtMost(30) / 30f * 360f).coerceAtLeast(4f),
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Text(text = "🔥", fontSize = 26.sp)
            }
        }
    }
}

@Composable
private fun QuickActionsSection(navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "Quick Actions",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionItem(
                icon = Icons.Default.SmartToy,
                label = "AI Chat",
                bgColor = Color(0xFF2D2060),
                iconTint = AccentLavender,
                onClick = { navController.navigate(Routes.AI_CHAT) }
            )
            QuickActionItem(
                icon = Icons.Default.Air,
                label = "Breathing",
                bgColor = Color(0xFF143530),
                iconTint = GreenTeal,
                onClick = { navController.navigate(Routes.BREATHING) }
            )
            QuickActionItem(
                icon = Icons.Default.Book,
                label = "Journal",
                bgColor = Color(0xFF132040),
                iconTint = BlueAccent,
                onClick = { }
            )
            QuickActionItem(
                icon = Icons.Default.Group,
                label = "Community",
                bgColor = Color(0xFF331A08),
                iconTint = OrangeAccent,
                onClick = { navController.navigate(Routes.COMMUNITY) }
            )
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    bgColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .background(bgColor, RoundedCornerShape(18.dp))
                .border(1.dp, iconTint.copy(alpha = 0.18f), RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TodayForYouSection(navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "Today for you",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Breathing exercise card
        TodayCard(
            emoji = "🌬️",
            iconBg = Color(0xFF143530),
            title = "Take a deep breath",
            subtitle = "You've got this!",
            meta = "3 min  •  Breathing Exercise",
            onClick = { navController.navigate(Routes.BREATHING) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Motivational quote card
        TodayCard(
            emoji = "✨",
            iconBg = Color(0xFF2D2060),
            title = "Daily motivation",
            subtitle = "\"Small steps lead to big changes.\"",
            meta = "Tap to get today's quote"
        )
    }
}

@Composable
private fun TodayCard(
    emoji: String,
    iconBg: Color,
    title: String,
    subtitle: String,
    meta: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = meta,
                    fontSize = 11.sp,
                    color = TextHint
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(PrimaryPurple, CircleShape)
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    tint = TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun MindMitraBottomBar(navController: NavController) {
    val items = listOf(
        Triple("Home",     Icons.Default.Home,     Routes.HOME),
        Triple("Chat",     Icons.Default.Chat,     Routes.AI_CHAT),
        Triple("Journal",  Icons.Default.Book,     Routes.JOURNAL),
        Triple("Com",      Icons.Default.Group,    Routes.COMMUNITY),
        Triple("Profile",  Icons.Default.Person,   Routes.PROFILE),
    )

    NavigationBar(containerColor = BottomNavBg, tonalElevation = 0.dp) {
        items.forEachIndexed { index, (label, icon, route) ->
            val isSelected = index == 0 // Home is always selected on this screen
            NavigationBarItem(
                icon = { Icon(icon, label, modifier = Modifier.size(22.dp)) },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentLavender,
                    selectedTextColor = AccentLavender,
                    unselectedIconColor = TextHint,
                    unselectedTextColor = TextHint,
                    indicatorColor = PrimaryPurple.copy(alpha = 0.18f)
                )
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Sidebar Drawer
// ═══════════════════════════════════════════════════════════════════════════════

private data class FeatureItem(
    val icon: ImageVector,
    val iconTint: Color,
    val iconBg: Color,
    val title: String,
    val description: String
)

@Composable
fun SidebarDrawer(onClose: () -> Unit) {
    val features = listOf(
        FeatureItem(Icons.Default.SmartToy,   Color(0xFF7BBFFF), Color(0xFF0D1E38), "AI Support",     "Talk to AI that understands you"),
        FeatureItem(Icons.Default.TrendingUp, Color(0xFFFF9F7B), Color(0xFF2A1208), "Mood Tracking",  "Track your mood and patterns"),
        FeatureItem(Icons.Default.Favorite,   Color(0xFFFF8FAB), Color(0xFF2A0D18), "Personalized",   "Get tips & exercises, just for you"),
        FeatureItem(Icons.Default.Lock,       Color(0xFFA78BFA), Color(0xFF1A1040), "Private & Safe", "Your data is secure and private"),
    )

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.88f)
            .background(Color(0xFF0D0B28))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // ── Header ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                Brush.radialGradient(listOf(Color(0xFF7C5CE7), Color(0xFF4A3080))),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "MindMitra",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(Color(0xFF1A1840), CircleShape)
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // ── Why MindMitra title ───────────────────────────────────────────
            Text(
                text = "Why MindMitra?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            // ── 2×2 feature cards ─────────────────────────────────────────────
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                features.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { feature ->
                            SidebarFeatureCard(feature, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // ── Mascot + quote ────────────────────────────────────────────────
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Canvas-drawn mascot
                Box(modifier = Modifier.size(130.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2f
                        val bw = size.width * 0.72f
                        val bh = size.height * 0.74f
                        val bLeft = cx - bw / 2f
                        val bTop = size.height * 0.20f

                        // Soft ambient glow
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x507C5CE7), Color.Transparent),
                                center = Offset(cx, size.height * 0.55f),
                                radius = size.width * 0.55f
                            ),
                            radius = size.width * 0.55f,
                            center = Offset(cx, size.height * 0.55f)
                        )

                        // Body
                        drawOval(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF9B72CF), Color(0xFF6A3FAE), Color(0xFF4A2888)),
                                center = Offset(cx, bTop + bh * 0.42f)
                            ),
                            topLeft = Offset(bLeft, bTop),
                            size = Size(bw, bh)
                        )

                        // Belly highlight
                        drawOval(
                            color = Color(0xFF3A1888).copy(alpha = 0.30f),
                            topLeft = Offset(cx - bw * 0.20f, bTop + bh * 0.50f),
                            size = Size(bw * 0.40f, bh * 0.28f)
                        )

                        // Body shine (top-left highlight)
                        drawOval(
                            color = Color.White.copy(alpha = 0.14f),
                            topLeft = Offset(bLeft + bw * 0.12f, bTop + bh * 0.06f),
                            size = Size(bw * 0.28f, bh * 0.18f)
                        )

                        // Sprout stem
                        val stemBaseX = cx + size.width * 0.05f
                        val stemBaseY = bTop + 4f
                        drawLine(
                            color = Color(0xFF5CB85C),
                            start = Offset(stemBaseX, stemBaseY),
                            end = Offset(stemBaseX - 4f, bTop - size.height * 0.12f),
                            strokeWidth = 3.5f,
                            cap = StrokeCap.Round
                        )
                        // Leaf
                        drawOval(
                            color = Color(0xFF72CC6A),
                            topLeft = Offset(stemBaseX - 14f, bTop - size.height * 0.20f),
                            size = Size(22f, 14f)
                        )
                        drawOval(
                            color = Color(0xFF5CB85C),
                            topLeft = Offset(stemBaseX - 4f, bTop - size.height * 0.18f),
                            size = Size(16f, 10f)
                        )

                        // Eyes — closed curved arcs
                        val eyeY = bTop + bh * 0.38f
                        val eyeSize = Size(size.width * 0.13f, size.height * 0.07f)
                        // Left eye
                        drawArc(
                            color = Color(0xFF1A0840),
                            startAngle = 185f, sweepAngle = 170f, useCenter = false,
                            topLeft = Offset(cx - bw * 0.28f, eyeY),
                            size = eyeSize,
                            style = Stroke(width = 3f, cap = StrokeCap.Round)
                        )
                        // Right eye
                        drawArc(
                            color = Color(0xFF1A0840),
                            startAngle = 185f, sweepAngle = 170f, useCenter = false,
                            topLeft = Offset(cx + bw * 0.10f, eyeY),
                            size = eyeSize,
                            style = Stroke(width = 3f, cap = StrokeCap.Round)
                        )

                        // Rosy cheeks
                        drawCircle(Color(0xFFFF80AB).copy(0.28f), 9f,
                            Offset(cx - bw * 0.28f, eyeY + size.height * 0.10f))
                        drawCircle(Color(0xFFFF80AB).copy(0.28f), 9f,
                            Offset(cx + bw * 0.28f, eyeY + size.height * 0.10f))

                        // Arms — small rounded stubs at sides
                        drawOval(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFF8B62BF), Color(0xFF4A2888)),
                                center = Offset(bLeft - 10f, bTop + bh * 0.65f)
                            ),
                            topLeft = Offset(bLeft - 20f, bTop + bh * 0.52f),
                            size = Size(28f, 22f)
                        )
                        drawOval(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFF8B62BF), Color(0xFF4A2888)),
                                center = Offset(bLeft + bw + 10f, bTop + bh * 0.65f)
                            ),
                            topLeft = Offset(bLeft + bw - 8f, bTop + bh * 0.52f),
                            size = Size(28f, 22f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Quote text
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "You don't have to have it all figured out. Just take the next small step.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "💜",
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ── Help section ──────────────────────────────────────────────────
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFF1A1840), RoundedCornerShape(16.dp))
                    .border(1.dp, PrimaryPurple.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
                    .clickable { }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF0D1E38), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headset,
                            contentDescription = null,
                            tint = Color(0xFF7BBFFF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Help & Support",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "We're here to help anytime",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Text(text = "›", fontSize = 20.sp, color = TextHint)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SidebarFeatureCard(feature: FeatureItem, modifier: Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFF13112E), RoundedCornerShape(16.dp))
            .border(1.dp, PrimaryPurple.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(feature.iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = feature.title,
                    tint = feature.iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = feature.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = feature.description,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )
        }
    }
}
