package com.mindmitra.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mindmitra.app.ui.viewmodel.AuthViewModel
import com.mindmitra.app.ui.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun StreakMapScreen(navController: NavController, userViewModel: UserViewModel) {
    val authViewModel: AuthViewModel = viewModel()
    val isMale = authViewModel.isMale

    val bg       = if (isMale) Color(0xFF080820) else Color(0xFFFFF0F5)
    val card     = if (isMale) Color(0xFF12122E) else Color(0xFFFFFFFF)
    val border   = if (isMale) Color(0xFF2A2850) else Color(0xFFFFD6E7)
    val primary  = if (isMale) Color(0xFF7C6FCD) else Color(0xFFEF5DA8)
    val textMain = if (isMale) Color(0xFFEEEEFF) else Color(0xFF3A1A2E)
    val textSub  = if (isMale) Color(0xFF8080A0) else Color(0xFFAA6680)
    val dimCell  = if (isMale) Color(0xFF1E1C4A) else Color(0xFFFFE4EF)

    var selectedTab by remember { mutableStateOf(0) }
    val openDates = remember { userViewModel.getOpenDates() }
    val fmt = remember { SimpleDateFormat("yyyyMMdd", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(38.dp).background(card, CircleShape)
                    .border(1.dp, border, CircleShape).clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, "Back", tint = primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text("Streak Map", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textMain)
                Text("${userViewModel.streakCount} day current streak 🔥", fontSize = 13.sp, color = textSub)
            }
        }

        // Tab switcher
        Row(
            modifier = Modifier.padding(horizontal = 20.dp)
                .background(card, RoundedCornerShape(30.dp))
                .border(1.dp, border, RoundedCornerShape(30.dp))
                .padding(4.dp)
        ) {
            listOf("30 Days", "6 Months", "Year").forEachIndexed { i, label ->
                Box(
                    modifier = Modifier.weight(1f)
                        .background(if (selectedTab == i) primary else Color.Transparent, RoundedCornerShape(26.dp))
                        .clickable { selectedTab = i }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                        color = if (selectedTab == i) Color.White else textSub)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Summary stats row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StreakStatCard("🔥", "${userViewModel.streakCount}", "Current\nStreak", primary, card, border, textMain, textSub, Modifier.weight(1f))
                    StreakStatCard("📅", "${openDates.size}", "Total\nDays", primary, card, border, textMain, textSub, Modifier.weight(1f))
                    val longest = computeLongestStreak(openDates)
                    StreakStatCard("🏆", "$longest", "Best\nStreak", primary, card, border, textMain, textSub, Modifier.weight(1f))
                }
            }

            item {
                when (selectedTab) {
                    0 -> DaysCalendar(openDates, fmt, primary, dimCell, card, border, textMain, textSub)
                    1 -> MonthsChart(openDates, fmt, primary, dimCell, card, border, textMain, textSub)
                    else -> YearHeatmap(openDates, fmt, primary, dimCell, card, border, textMain, textSub)
                }
            }

            item {
                // Motivational message
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = card),
                    border = androidx.compose.foundation.BorderStroke(1.dp, border)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalFireDepartment, null, tint = Color(0xFFFF6B35), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                if (userViewModel.streakCount >= 7) "You're on fire! 🔥 Keep it up!" else "Come back tomorrow to grow your streak!",
                                fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textMain
                            )
                            Text("Open the app every day to maintain your streak.", fontSize = 12.sp, color = textSub)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// ── 30-Day calendar grid ─────────────────────────────────────────────────────

@Composable
private fun DaysCalendar(
    openDates: Set<String>, fmt: SimpleDateFormat,
    primary: Color, dimCell: Color, card: Color, border: Color, textMain: Color, textSub: Color
) {
    val cal = Calendar.getInstance()
    val days = (29 downTo 0).map { offset ->
        val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -offset) }
        Pair(fmt.format(c.time), c.get(Calendar.DAY_OF_MONTH))
    }
    val dayLabels = listOf("S", "M", "T", "W", "T", "F", "S")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = card),
        border = androidx.compose.foundation.BorderStroke(1.dp, border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Last 30 Days", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = textMain)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${openDates.count { it in days.map { d -> d.first } }} / 30 days active",
                fontSize = 12.sp, color = textSub)
            Spacer(modifier = Modifier.height(14.dp))

            // Day-of-week headers
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                dayLabels.forEach { Text(it, fontSize = 11.sp, color = textSub, fontWeight = FontWeight.Medium) }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Pad start so first cell aligns to correct weekday column
            val firstDayOfWeek = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).parse(days.first().first) ?: Date()
            }.get(Calendar.DAY_OF_WEEK) - 1  // 0=Sun

            val padded = List(firstDayOfWeek) { null } + days.map { it }

            val rows = padded.chunked(7)
            rows.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    repeat(7) { col ->
                        val cell = if (col < row.size) row[col] else null
                        if (cell == null) {
                            Box(modifier = Modifier.size(34.dp))
                        } else {
                            val isOpen = cell.first in openDates
                            Box(
                                modifier = Modifier.size(34.dp)
                                    .background(if (isOpen) primary else dimCell, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${cell.second}", fontSize = 12.sp,
                                    fontWeight = if (isOpen) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isOpen) Color.White else textSub
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(12.dp).background(primary, RoundedCornerShape(3.dp)))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Active day", fontSize = 11.sp, color = textSub)
                Spacer(modifier = Modifier.width(16.dp))
                Box(modifier = Modifier.size(12.dp).background(dimCell, RoundedCornerShape(3.dp)))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Missed", fontSize = 11.sp, color = textSub)
            }
        }
    }
}

// ── 6-Month bar chart ─────────────────────────────────────────────────────────

@Composable
private fun MonthsChart(
    openDates: Set<String>, fmt: SimpleDateFormat,
    primary: Color, dimCell: Color, card: Color, border: Color, textMain: Color, textSub: Color
) {
    val monthFmt = SimpleDateFormat("yyyyMM", Locale.getDefault())
    val labelFmt = SimpleDateFormat("MMM", Locale.getDefault())

    // Build last 6 months
    val months = (5 downTo 0).map { offset ->
        val c = Calendar.getInstance().apply { add(Calendar.MONTH, -offset) }
        Triple(
            monthFmt.format(c.time),  // key e.g. "202504"
            labelFmt.format(c.time),  // label e.g. "Apr"
            c.getActualMaximum(Calendar.DAY_OF_MONTH)  // days in month
        )
    }

    // Count active days per month
    val counts = months.map { (key, label, maxDays) ->
        val active = openDates.count { it.startsWith(key) }
        Triple(label, active, maxDays)
    }
    val maxVal = counts.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = card),
        border = androidx.compose.foundation.BorderStroke(1.dp, border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Last 6 Months", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = textMain)
            Text("Active days per month", fontSize = 12.sp, color = textSub)
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth().height(140.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                counts.forEach { (label, active, maxDays) ->
                    val frac = active.toFloat() / maxVal.toFloat()
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$active", fontSize = 11.sp, color = textSub)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height((frac * 100).coerceAtLeast(4f).dp)
                                .background(if (active > 0) primary else dimCell, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(label, fontSize = 11.sp, color = textSub, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

// ── Year heatmap (52-week grid) ───────────────────────────────────────────────

@Composable
private fun YearHeatmap(
    openDates: Set<String>, fmt: SimpleDateFormat,
    primary: Color, dimCell: Color, card: Color, border: Color, textMain: Color, textSub: Color
) {
    val totalDays = 364
    val days = (totalDays - 1 downTo 0).map { offset ->
        val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -offset) }
        fmt.format(c.time)
    }
    val weeks = days.chunked(7)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = card),
        border = androidx.compose.foundation.BorderStroke(1.dp, border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Past Year", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = textMain)
            Text("${openDates.count { it in days }} days active this year",
                fontSize = 12.sp, color = textSub)
            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                weeks.forEach { week ->
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        week.forEach { dateKey ->
                            val isOpen = dateKey in openDates
                            Box(
                                modifier = Modifier.size(10.dp)
                                    .background(
                                        if (isOpen) primary else dimCell,
                                        RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Less", fontSize = 10.sp, color = textSub)
                Spacer(modifier = Modifier.width(6.dp))
                listOf(0.15f, 0.35f, 0.6f, 0.85f, 1f).forEach { alpha ->
                    Box(modifier = Modifier.size(10.dp).background(primary.copy(alpha = alpha), RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(3.dp))
                }
                Text("More", fontSize = 10.sp, color = textSub)
            }
        }
    }
}

// ── Stat card ─────────────────────────────────────────────────────────────────

@Composable
private fun StreakStatCard(
    emoji: String, value: String, label: String,
    primary: Color, card: Color, border: Color, textMain: Color, textSub: Color,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = card),
        border = androidx.compose.foundation.BorderStroke(1.dp, border)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = primary)
            Text(label, fontSize = 11.sp, color = textSub, lineHeight = 15.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

// ── Helper: compute longest streak from openDates set ────────────────────────

private fun computeLongestStreak(openDates: Set<String>): Int {
    if (openDates.isEmpty()) return 0
    val fmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val sorted = openDates.mapNotNull { runCatching { fmt.parse(it) }.getOrNull() }
        .sorted()
    var best = 1; var cur = 1
    val cal = Calendar.getInstance()
    for (i in 1 until sorted.size) {
        cal.time = sorted[i - 1]; cal.add(Calendar.DAY_OF_YEAR, 1)
        val nextExpected = fmt.format(cal.time)
        if (fmt.format(sorted[i]) == nextExpected) { cur++; if (cur > best) best = cur }
        else cur = 1
    }
    return best
}
