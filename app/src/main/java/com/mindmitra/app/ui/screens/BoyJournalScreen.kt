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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
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
import com.mindmitra.app.ui.theme.PrimaryPurple
import com.mindmitra.app.ui.theme.TextHint
import com.mindmitra.app.ui.theme.TextPrimary
import com.mindmitra.app.ui.theme.TextSecondary
import com.mindmitra.app.ui.viewmodel.UserViewModel

@Composable
fun BoyJournalScreen(navController: NavController, userViewModel: UserViewModel) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = { BoyJournalBottomBar(navController = navController) },
        containerColor = DeepNavy
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DeepNavy)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("My Journal 📖", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            // Tab toggle
            Row(
                modifier = Modifier.padding(horizontal = 20.dp)
                    .background(CardSurface, RoundedCornerShape(30.dp))
                    .border(1.dp, PrimaryPurple.copy(0.25f), RoundedCornerShape(30.dp))
                    .padding(4.dp)
            ) {
                listOf("My Journal", "Insights").forEachIndexed { index, label ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (selectedTab == index) PrimaryPurple else Color.Transparent, RoundedCornerShape(26.dp))
                            .clickable { selectedTab = index }
                            .padding(horizontal = 16.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                            color = if (selectedTab == index) Color.White else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) BoyJournalWriteTab(userViewModel)
            else BoyJournalInsightsTab(userViewModel)
        }
    }
}

@Composable
private fun BoyJournalWriteTab(userViewModel: UserViewModel) {
    val moodOptions = listOf("😊", "😌", "😢", "😡", "💪")
    var titleText by remember { mutableStateOf("") }
    var writeText by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("😊") }
    val entries = userViewModel.journalEntries

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Write card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryPurple.copy(0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Write today's entry ✍️", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AccentLavender)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Title field
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF12103A), RoundedCornerShape(10.dp))
                            .border(1.dp, PrimaryPurple.copy(0.3f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        if (titleText.isEmpty()) Text("Title (optional)", fontSize = 13.sp, color = TextHint)
                        BasicTextField(
                            value = titleText,
                            onValueChange = { titleText = it },
                            textStyle = TextStyle(fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium),
                            cursorBrush = SolidColor(AccentLavender),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Content field
                    Box(
                        modifier = Modifier
                            .fillMaxWidth().height(110.dp)
                            .background(Color(0xFF12103A), RoundedCornerShape(12.dp))
                            .border(1.dp, PrimaryPurple.copy(0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        if (writeText.isEmpty()) Text("How was your day? What are you feeling?...", fontSize = 13.sp, color = TextHint)
                        BasicTextField(
                            value = writeText, onValueChange = { writeText = it },
                            textStyle = TextStyle(fontSize = 13.sp, color = TextPrimary),
                            cursorBrush = SolidColor(AccentLavender),
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mood picker
                    Text("How are you feeling?", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        moodOptions.forEach { mood ->
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        if (mood == selectedMood) PrimaryPurple else Color(0xFF12103A),
                                        CircleShape
                                    )
                                    .border(
                                        1.5.dp,
                                        if (mood == selectedMood) AccentLavender else PrimaryPurple.copy(0.25f),
                                        CircleShape
                                    )
                                    .clickable { selectedMood = mood },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(mood, fontSize = 20.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            if (writeText.isNotBlank()) {
                                userViewModel.addJournalEntry(
                                    title = titleText.trim().ifBlank { "Today's Entry" },
                                    content = writeText.trim(),
                                    mood = selectedMood
                                )
                                titleText = ""
                                writeText = ""
                                selectedMood = "😊"
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                    ) {
                        Text("Save Entry 💜", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        if (entries.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📝", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No entries yet", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                        Text("Write your first journal entry above!", fontSize = 13.sp, color = TextHint, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            item {
                Text(
                    "Recent Entries (${entries.size})",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary
                )
            }
            items(entries) { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryPurple.copy(0.18f))
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier.size(46.dp).background(Color(0xFF1E1040), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(entry.mood, fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    entry.title,
                                    fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(entry.dateTime, fontSize = 11.sp, color = AccentLavender)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                entry.content.take(80) + if (entry.content.length > 80) "..." else "",
                                fontSize = 13.sp, color = TextSecondary, lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun BoyJournalInsightsTab(userViewModel: UserViewModel) {
    val entries = userViewModel.journalEntries
    val hasEntries = entries.isNotEmpty()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stats row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BoyStatCard("${userViewModel.journalCount}", "Journal\nEntries", "📝", Modifier.weight(1f))
            BoyStatCard("${userViewModel.streakCount}", "Day\nStreak", "🔥", Modifier.weight(1f))
            BoyStatCard("${userViewModel.breathingSessionsCompleted}", "Breathing\nSessions", "🫁", Modifier.weight(1f))
        }

        if (!hasEntries) {
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryPurple.copy(0.2f))
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📈", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No data yet", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                        Text("Write journal entries to see insights", fontSize = 13.sp, color = TextHint, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            // Mood distribution from real entries
            val moodCounts = entries.groupBy { it.mood }
            val totalEntries = entries.size.toFloat()

            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryPurple.copy(0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Mood Distribution 📊", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(14.dp))
                    moodCounts.entries.sortedByDescending { it.value.size }.take(5).forEach { (mood, list) ->
                        val pct = list.size / totalEntries
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 5.dp)) {
                            Text(mood, fontSize = 18.sp, modifier = Modifier.width(36.dp))
                            Box(
                                modifier = Modifier.weight(1f).height(8.dp)
                                    .background(Color(0xFF1E1C4A), RoundedCornerShape(4.dp))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(pct).height(8.dp)
                                        .background(
                                            Brush.horizontalGradient(listOf(Color(0xFF9B8BFA), Color(0xFF6C5CE7))),
                                            RoundedCornerShape(4.dp)
                                        )
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("${list.size}x", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.width(28.dp))
                        }
                    }
                }
            }

            // Recent activity line chart (last 7 entries)
            if (entries.size >= 2) {
                val recentEntries = entries.take(7).reversed()
                val moodScores = recentEntries.map { entry ->
                    when (entry.mood) { "😊" -> 0.85f; "💪" -> 0.9f; "😌" -> 0.7f; "😢" -> 0.25f; "😡" -> 0.15f; else -> 0.5f }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryPurple.copy(0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Recent Mood Trend 📈", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            recentEntries.forEach { Text(it.mood, fontSize = 14.sp) }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                            val w = size.width; val h = size.height
                            if (moodScores.size < 2) return@Canvas
                            val stepX = w / (moodScores.size - 1).toFloat()
                            val pts = moodScores.mapIndexed { i, v -> Offset(i * stepX, h - v * h * 0.85f - h * 0.05f) }
                            val fill = Path().apply {
                                moveTo(pts[0].x, h); pts.forEach { lineTo(it.x, it.y) }; lineTo(pts.last().x, h); close()
                            }
                            drawPath(fill, Brush.verticalGradient(listOf(Color(0x506C5CE7), Color(0x106C5CE7)), 0f, h))
                            val line = Path().apply {
                                moveTo(pts[0].x, pts[0].y)
                                for (i in 1 until pts.size) {
                                    val cx = (pts[i-1].x + pts[i].x) / 2f
                                    quadraticBezierTo(pts[i-1].x, pts[i-1].y, cx, (pts[i-1].y + pts[i].y) / 2f)
                                }
                                lineTo(pts.last().x, pts.last().y)
                            }
                            drawPath(line, Brush.horizontalGradient(listOf(Color(0xFF9B8BFA), Color(0xFF6C5CE7))), style = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round))
                            pts.forEach { drawCircle(Color(0xFF6C5CE7), 4.dp.toPx(), it); drawCircle(Color.White, 2.dp.toPx(), it) }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun BoyStatCard(value: String, label: String, emoji: String, modifier: Modifier) {
    Card(
        modifier = modifier, shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryPurple.copy(0.2f))
    ) {
        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AccentLavender)
            Spacer(modifier = Modifier.height(3.dp))
            Text(label, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun BoyJournalBottomBar(navController: NavController) {
    val items = listOf(
        Triple("Home",    Icons.Default.Home,    Routes.HOME),
        Triple("Chat",    Icons.Default.Chat,    Routes.AI_CHAT),
        Triple("Journal", Icons.Default.Book,    Routes.JOURNAL),
        Triple("Com",     Icons.Default.Group,   Routes.COMMUNITY),
        Triple("Profile", Icons.Default.Person,  Routes.PROFILE),
    )
    NavigationBar(containerColor = BottomNavBg, tonalElevation = 0.dp) {
        items.forEachIndexed { index, (label, icon, route) ->
            val isSelected = index == 2
            NavigationBarItem(
                icon = { Icon(icon, label, modifier = Modifier.size(22.dp)) },
                label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal) },
                selected = isSelected,
                onClick = {
                    if (!isSelected) navController.navigate(route) {
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true; restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentLavender, selectedTextColor = AccentLavender,
                    unselectedIconColor = TextHint, unselectedTextColor = TextHint,
                    indicatorColor = PrimaryPurple.copy(alpha = 0.18f)
                )
            )
        }
    }
}
