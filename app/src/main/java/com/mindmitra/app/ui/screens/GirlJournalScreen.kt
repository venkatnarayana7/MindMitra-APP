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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.mindmitra.app.ui.theme.GirlBg
import com.mindmitra.app.ui.theme.GirlBorder
import com.mindmitra.app.ui.theme.GirlCard
import com.mindmitra.app.ui.theme.GirlPrimary
import com.mindmitra.app.ui.theme.GirlPrimaryDim
import com.mindmitra.app.ui.theme.GirlSurface
import com.mindmitra.app.ui.theme.GirlTextDark
import com.mindmitra.app.ui.theme.GirlTextLight
import com.mindmitra.app.ui.theme.GirlTextMid
import com.mindmitra.app.ui.viewmodel.UserViewModel

@Composable
fun GirlJournalScreen(navController: NavController, userViewModel: UserViewModel) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = { GirlBottomBar(navController = navController, selectedIndex = 2) },
        containerColor = GirlBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(GirlBg)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("My Space 📖", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GirlTextDark)
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .background(GirlCard, RoundedCornerShape(30.dp))
                    .border(1.dp, GirlBorder, RoundedCornerShape(30.dp))
                    .padding(4.dp)
            ) {
                listOf("My Journal", "Insights").forEachIndexed { index, label ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (selectedTab == index) GirlPrimary else Color.Transparent, RoundedCornerShape(26.dp))
                            .clickable { selectedTab = index }
                            .padding(horizontal = 16.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                            color = if (selectedTab == index) Color.White else GirlTextMid
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) GirlJournalWriteTab(userViewModel)
            else GirlJournalInsightsTab(userViewModel)
        }
    }
}

@Composable
private fun GirlJournalWriteTab(userViewModel: UserViewModel) {
    val moodOptions = listOf("😊", "😌", "🌸", "😢", "💪")
    var titleText by remember { mutableStateOf("") }
    var writeText by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("🌸") }
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
                colors = CardDefaults.cardColors(containerColor = GirlCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, GirlBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Write today's entry ✍️", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = GirlPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Title field
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GirlSurface, RoundedCornerShape(10.dp))
                            .border(1.dp, GirlBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        if (titleText.isEmpty()) Text("Title (optional)", fontSize = 13.sp, color = GirlTextLight)
                        BasicTextField(
                            value = titleText,
                            onValueChange = { titleText = it },
                            textStyle = TextStyle(fontSize = 13.sp, color = GirlTextDark, fontWeight = FontWeight.Medium),
                            cursorBrush = SolidColor(GirlPrimary),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Content field
                    Box(
                        modifier = Modifier
                            .fillMaxWidth().height(110.dp)
                            .background(GirlSurface, RoundedCornerShape(12.dp))
                            .border(1.dp, GirlBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        if (writeText.isEmpty()) Text("How was your day? What are you feeling?...", fontSize = 13.sp, color = GirlTextLight)
                        BasicTextField(
                            value = writeText,
                            onValueChange = { writeText = it },
                            textStyle = TextStyle(fontSize = 13.sp, color = GirlTextDark),
                            cursorBrush = SolidColor(GirlPrimary),
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mood picker
                    Text("How are you feeling?", fontSize = 12.sp, color = GirlTextMid)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        moodOptions.forEach { mood ->
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        if (mood == selectedMood) GirlPrimary else GirlSurface,
                                        CircleShape
                                    )
                                    .border(
                                        1.5.dp,
                                        if (mood == selectedMood) GirlPrimary else GirlBorder,
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
                                selectedMood = "🌸"
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GirlPrimary)
                    ) {
                        Text("Save Entry 💗", fontWeight = FontWeight.Bold, color = Color.White)
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
                        Text("🌸", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No entries yet", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = GirlTextMid)
                        Text("Write your first journal entry above!", fontSize = 13.sp, color = GirlTextLight, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            item {
                Text(
                    "Recent Entries (${entries.size})",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = GirlTextDark
                )
            }
            items(entries) { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GirlCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GirlBorder)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier.size(46.dp).background(GirlPrimaryDim, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(entry.mood, fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = GirlTextDark)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(entry.dateTime, fontSize = 11.sp, color = GirlPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                entry.content.take(80) + if (entry.content.length > 80) "..." else "",
                                fontSize = 13.sp, color = GirlTextMid, lineHeight = 18.sp
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
private fun GirlJournalInsightsTab(userViewModel: UserViewModel) {
    val entries = userViewModel.journalEntries
    val hasEntries = entries.isNotEmpty()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stats row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GirlStatCard("${userViewModel.journalCount}", "Journal\nEntries", "📝", Modifier.weight(1f))
            GirlStatCard("${userViewModel.streakCount}", "Day\nStreak", "🔥", Modifier.weight(1f))
            GirlStatCard("${userViewModel.breathingSessionsCompleted}", "Breathing\nSessions", "🫁", Modifier.weight(1f))
        }

        if (!hasEntries) {
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GirlCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, GirlBorder)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💗", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No data yet", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = GirlTextMid)
                        Text("Write journal entries to see insights", fontSize = 13.sp, color = GirlTextLight, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            // Mood distribution from real entries
            val moodCounts = entries.groupBy { it.mood }
            val totalEntries = entries.size.toFloat()

            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GirlCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, GirlBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Mood Distribution 📊", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = GirlTextDark)
                    Spacer(modifier = Modifier.height(14.dp))
                    moodCounts.entries.sortedByDescending { it.value.size }.take(5).forEach { (mood, list) ->
                        val pct = list.size / totalEntries
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 5.dp)) {
                            Text(mood, fontSize = 18.sp, modifier = Modifier.width(36.dp))
                            Box(
                                modifier = Modifier.weight(1f).height(8.dp)
                                    .background(GirlSurface, RoundedCornerShape(4.dp))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(pct).height(8.dp)
                                        .background(
                                            Brush.horizontalGradient(listOf(Color(0xFFFFB3D1), GirlPrimary)),
                                            RoundedCornerShape(4.dp)
                                        )
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("${list.size}x", fontSize = 12.sp, color = GirlTextMid, modifier = Modifier.width(28.dp))
                        }
                    }
                }
            }

            // Mood trend chart (last 7 entries)
            if (entries.size >= 2) {
                val recentEntries = entries.take(7).reversed()
                val moodScores = recentEntries.map { entry ->
                    when (entry.mood) { "😊" -> 0.85f; "💪" -> 0.9f; "🌸" -> 0.75f; "😌" -> 0.7f; "😢" -> 0.25f; else -> 0.5f }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GirlCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GirlBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Recent Mood Trend 📈", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = GirlTextDark)
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
                            drawPath(fill, Brush.verticalGradient(listOf(Color(0x50EF5DA8), Color(0x10EF5DA8)), 0f, h))
                            val line = Path().apply {
                                moveTo(pts[0].x, pts[0].y)
                                for (i in 1 until pts.size) {
                                    val cx = (pts[i-1].x + pts[i].x) / 2f
                                    quadraticBezierTo(pts[i-1].x, pts[i-1].y, cx, (pts[i-1].y + pts[i].y) / 2f)
                                }
                                lineTo(pts.last().x, pts.last().y)
                            }
                            drawPath(line, Brush.horizontalGradient(listOf(Color(0xFFFFB3D1), Color(0xFFEF5DA8))), style = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round))
                            pts.forEach { drawCircle(Color(0xFFEF5DA8), 4.dp.toPx(), it); drawCircle(Color.White, 2.dp.toPx(), it) }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun GirlStatCard(value: String, label: String, emoji: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GirlCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GirlBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GirlPrimary)
            Spacer(modifier = Modifier.height(3.dp))
            Text(label, fontSize = 11.sp, color = GirlTextMid, lineHeight = 15.sp, textAlign = TextAlign.Center)
        }
    }
}
