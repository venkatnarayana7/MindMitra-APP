package com.mindmitra.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mindmitra.app.navigation.Routes
import com.mindmitra.app.ui.theme.AccentLavender
import com.mindmitra.app.ui.theme.BottomNavBg
import com.mindmitra.app.ui.theme.CardSurface
import com.mindmitra.app.ui.theme.DeepNavy
import com.mindmitra.app.ui.theme.GreenTeal
import com.mindmitra.app.ui.theme.PrimaryPurple
import com.mindmitra.app.ui.theme.TextHint
import com.mindmitra.app.ui.theme.TextPrimary
import com.mindmitra.app.ui.theme.TextSecondary

@Composable
fun AnalysisScreen(navController: NavController) {
    Scaffold(
        bottomBar = { AnalysisBottomBar(navController = navController) },
        containerColor = DeepNavy
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DeepNavy),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item { AnalysisHeader() }
            item { Spacer(modifier = Modifier.height(6.dp)) }
            item { MoodOverviewCard() }
            item { Spacer(modifier = Modifier.height(14.dp)) }
            item { StatsRow() }
            item { Spacer(modifier = Modifier.height(14.dp)) }
            item { TopEmotionCard() }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

// ─── Header ──────────────────────────────────────────────────────────────────

@Composable
private fun AnalysisHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Your Progress",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Track your mental wellness journey",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        Box(
            modifier = Modifier
                .background(CardSurface, RoundedCornerShape(20.dp))
                .border(1.dp, PrimaryPurple.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "This Week",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

// ─── Mood overview card with line chart ──────────────────────────────────────

@Composable
private fun MoodOverviewCard() {
    val moodData = listOf(3.0f, 3.5f, 4.2f, 3.8f, 4.5f, 4.8f, 4.0f)
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val moodEmojis = listOf("😔", "😐", "🙂", "😐", "😊", "😄", "🙂")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Mood Overview",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Better than\nlast week",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.End,
                        lineHeight = 14.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF0D2B1A), RoundedCornerShape(20.dp))
                            .border(1.dp, GreenTeal.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "+23%",
                            fontSize = 11.sp,
                            color = GreenTeal,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mood line chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val chartWidth = size.width
                val chartHeight = size.height
                val minVal = 1f
                val maxVal = 5f
                val range = maxVal - minVal
                val paddingTop = chartHeight * 0.08f
                val paddingBottom = chartHeight * 0.08f
                val usableHeight = chartHeight - paddingTop - paddingBottom

                val points = moodData.mapIndexed { i, value ->
                    val x = if (moodData.size > 1) i * chartWidth / (moodData.size - 1) else chartWidth / 2f
                    val y = paddingTop + usableHeight - ((value - minVal) / range) * usableHeight
                    Offset(x, y)
                }

                // Faint horizontal grid lines
                listOf(0.25f, 0.5f, 0.75f).forEach { fraction ->
                    val y = paddingTop + usableHeight * (1f - fraction)
                    drawLine(
                        color = Color.White.copy(alpha = 0.05f),
                        start = Offset(0f, y),
                        end = Offset(chartWidth, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Smooth path via midpoint technique
                val linePath = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 0 until points.size - 1) {
                        val cur = points[i]
                        val next = points[i + 1]
                        val midX = (cur.x + next.x) / 2f
                        val midY = (cur.y + next.y) / 2f
                        if (i == 0) {
                            quadraticBezierTo(cur.x, cur.y, midX, midY)
                        } else if (i == points.size - 2) {
                            quadraticBezierTo(cur.x, cur.y, next.x, next.y)
                        } else {
                            quadraticBezierTo(cur.x, cur.y, midX, midY)
                        }
                    }
                }

                // Gradient fill under curve
                val fillPath = Path().apply {
                    addPath(linePath)
                    lineTo(points.last().x, chartHeight)
                    lineTo(points.first().x, chartHeight)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(PrimaryPurple.copy(alpha = 0.45f), Color.Transparent),
                        startY = paddingTop,
                        endY = chartHeight
                    )
                )

                // Line stroke
                drawPath(
                    path = linePath,
                    color = AccentLavender,
                    style = Stroke(
                        width = 2.5.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Data dots
                points.forEach { point ->
                    drawCircle(color = Color.White, radius = 5.dp.toPx(), center = point)
                    drawCircle(color = PrimaryPurple, radius = 3.dp.toPx(), center = point)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Day labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEach { day ->
                    Text(
                        text = day,
                        fontSize = 10.sp,
                        color = TextHint,
                        modifier = Modifier.width(32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mood emoji row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                moodEmojis.forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 14.sp,
                        modifier = Modifier.width(32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ─── Stats row ────────────────────────────────────────────────────────────────

@Composable
private fun StatsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            topLabel = "Avg. Mood",
            emoji = "😊",
            bigText = "3.8/5",
            subText = "Good"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            topLabel = "Streak",
            emoji = "🔥",
            bigText = "7",
            subText = "Days"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            topLabel = "Check-ins",
            emoji = "📅",
            bigText = "14",
            subText = "This Month"
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    topLabel: String,
    emoji: String,
    bigText: String,
    subText: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = topLabel,
                fontSize = 10.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subText,
                fontSize = 11.sp,
                color = TextSecondary
            )
            Text(
                text = bigText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

// ─── Top emotion card with arc ring ──────────────────────────────────────────

@Composable
private fun TopEmotionCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Top Emotion",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Top Mood:",
                    fontSize = 11.sp,
                    color = TextHint
                )
                Text(
                    text = "Calm",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    lineHeight = 40.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "40% of the time",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            // Circular progress ring at 45%
            Box(
                modifier = Modifier.size(90.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 8.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)

                    // Track ring
                    drawCircle(
                        color = Color(0xFF2A2850),
                        radius = radius,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )

                    // Progress arc — 45% = 162 degrees
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(AccentLavender, PrimaryPurple, AccentLavender),
                            center = center
                        ),
                        startAngle = -90f,
                        sweepAngle = 162f,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "45%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}

// ─── Bottom navigation ────────────────────────────────────────────────────────

@Composable
private fun AnalysisBottomBar(navController: NavController) {
    val items = listOf(
        Triple("Home",     Icons.Default.Home,     Routes.HOME),
        Triple("Chat",     Icons.Default.Chat,     Routes.AI_CHAT),
        Triple("Journal",  Icons.Default.Book,     Routes.JOURNAL),
        Triple("Com",      Icons.Default.Group,    Routes.COMMUNITY),
        Triple("Profile",  Icons.Default.Person,   Routes.PROFILE),
    )

    NavigationBar(containerColor = BottomNavBg, tonalElevation = 0.dp) {
        items.forEachIndexed { index, (label, icon, route) ->
            val isSelected = index == 2 // Analysis tab
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
