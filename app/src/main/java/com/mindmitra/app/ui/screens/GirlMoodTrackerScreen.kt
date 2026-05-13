package com.mindmitra.app.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

private data class GirlMoodOption(val emoji: String, val label: String, val color: Color)

@Composable
fun GirlMoodTrackerScreen(navController: NavController) {
    val moods = listOf(
        GirlMoodOption("😁", "Amazing",    Color(0xFFFFD700)),
        GirlMoodOption("😊", "Good",       Color(0xFF4CAF50)),
        GirlMoodOption("😐", "Okay",       Color(0xFF2196F3)),
        GirlMoodOption("😢", "Sad",        Color(0xFF90CAF9)),
        GirlMoodOption("😰", "Anxious",    Color(0xFFFF9800)),
        GirlMoodOption("😠", "Angry",      Color(0xFFF44336)),
        GirlMoodOption("😴", "Tired",      Color(0xFF9C27B0)),
        GirlMoodOption("😓", "Stressed",   Color(0xFFFF5722)),
        GirlMoodOption("😩", "Overwhelmed",Color(0xFF607D8B)),
    )
    var selectedMood by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GirlBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
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

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "How are you feeling?",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = GirlTextDark,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Choose the emotion that best describes you right now 💗",
            fontSize = 14.sp,
            color = GirlTextMid,
            modifier = Modifier.padding(horizontal = 20.dp),
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // 3×3 mood grid
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            moods.chunked(3).forEach { rowMoods ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowMoods.forEach { mood ->
                        val isSelected = selectedMood == mood.label
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) mood.color.copy(alpha = 0.15f) else GirlCard,
                                    RoundedCornerShape(18.dp)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) mood.color else GirlBorder,
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .clickable { selectedMood = mood.label }
                                .padding(vertical = 18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(mood.emoji, fontSize = 30.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = mood.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) mood.color else GirlTextMid,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (selectedMood.isNotBlank()) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GirlPrimaryDim, RoundedCornerShape(16.dp))
                        .border(1.dp, GirlBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "You're feeling $selectedMood today. That's completely okay. 💗\nRemember, every feeling is valid.",
                        fontSize = 14.sp,
                        color = GirlTextDark,
                        lineHeight = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GirlPrimary)
                ) {
                    Text("Save Mood ✨", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
