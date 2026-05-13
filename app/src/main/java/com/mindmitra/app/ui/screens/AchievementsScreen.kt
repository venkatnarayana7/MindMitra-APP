package com.mindmitra.app.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mindmitra.app.ui.theme.AccentLavender
import com.mindmitra.app.ui.theme.CardSurface
import com.mindmitra.app.ui.theme.DeepNavy
import com.mindmitra.app.ui.theme.GirlBg
import com.mindmitra.app.ui.theme.GirlBorder
import com.mindmitra.app.ui.theme.GirlCard
import com.mindmitra.app.ui.theme.GirlPrimary
import com.mindmitra.app.ui.theme.GirlPrimaryDim
import com.mindmitra.app.ui.theme.GirlTextDark
import com.mindmitra.app.ui.theme.GirlTextMid
import com.mindmitra.app.ui.theme.PrimaryPurple
import com.mindmitra.app.ui.theme.TextHint
import com.mindmitra.app.ui.theme.TextPrimary
import com.mindmitra.app.ui.theme.TextSecondary
import com.mindmitra.app.ui.viewmodel.AuthViewModel
import com.mindmitra.app.ui.viewmodel.UserViewModel

private data class AchievementData(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val progress: Int,
    val total: Int,
    val unlockedColor: Color
)

@Composable
fun AchievementsScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val authViewModel: AuthViewModel = viewModel()
    val isMale = authViewModel.isMale

    // Colour scheme based on gender
    val bg         = if (isMale) DeepNavy      else GirlBg
    val card       = if (isMale) CardSurface   else GirlCard
    val border     = if (isMale) PrimaryPurple.copy(0.22f) else GirlBorder
    val titleColor = if (isMale) TextPrimary   else GirlTextDark
    val subColor   = if (isMale) TextSecondary else GirlTextMid
    val accent     = if (isMale) AccentLavender else GirlPrimary

    val achievements = listOf(
        AchievementData(
            id = "breath_royalty",
            emoji = if (isMale) "👑" else "👸",
            title = if (isMale) "Breath King" else "Breath Queen",
            description = "Complete 10 full breathing cycles",
            isUnlocked = userViewModel.isBreathAchievementUnlocked,
            progress = minOf(userViewModel.breathingSessionsCompleted, 10),
            total = 10,
            unlockedColor = if (isMale) Color(0xFFFFD700) else Color(0xFFEF5DA8)
        ),
        AchievementData(
            id = "streak_master",
            emoji = "🔥",
            title = "Streak Master",
            description = "Maintain a 7-day check-in streak",
            isUnlocked = userViewModel.streakCount >= 7,
            progress = minOf(userViewModel.streakCount, 7),
            total = 7,
            unlockedColor = Color(0xFFFF6B35)
        ),
        AchievementData(
            id = "journal_starter",
            emoji = "📝",
            title = "Journal Starter",
            description = "Write 5 journal entries",
            isUnlocked = userViewModel.journalCount >= 5,
            progress = minOf(userViewModel.journalCount, 5),
            total = 5,
            unlockedColor = Color(0xFF4ECDC4)
        ),
        AchievementData(
            id = "wellness_warrior",
            emoji = "⭐",
            title = if (isMale) "Wellness Warrior" else "Wellness Star",
            description = "Reach Level 3 (15 journal entries)",
            isUnlocked = userViewModel.userLevel >= 3,
            progress = minOf(userViewModel.journalCount, 15),
            total = 15,
            unlockedColor = if (isMale) AccentLavender else GirlPrimary
        ),
        AchievementData(
            id = "mood_tracker",
            emoji = "😊",
            title = "Mood Mapper",
            description = "Log your mood 10 times",
            isUnlocked = false,
            progress = 0,
            total = 10,
            unlockedColor = Color(0xFF4CAF50)
        ),
    )

    // Pulsing animation for unlocked badges
    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulse.animateFloat(
        initialValue = 1f, targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseScale"
    )
    val shimmer by pulse.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "shimmer"
    )

    Column(
        modifier = Modifier.fillMaxSize().background(bg).statusBarsPadding().navigationBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp)
                    .background(card, CircleShape)
                    .border(1.dp, border, CircleShape)
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, "Back", tint = accent, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text("Achievements 🏆", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = titleColor)
        }

        // Unlocked count
        val unlockedCount = achievements.count { it.isUnlocked }
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                .background(
                    Brush.horizontalGradient(
                        if (isMale) listOf(Color(0xFF2D2060), Color(0xFF1A1040))
                        else listOf(GirlPrimaryDim, GirlCard)
                    ),
                    RoundedCornerShape(16.dp)
                )
                .border(1.dp, border, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🏆", fontSize = 32.sp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("$unlockedCount / ${achievements.size} Unlocked", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = titleColor)
                    Text("Keep going to unlock more!", fontSize = 12.sp, color = subColor)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(achievements.size) { index ->
                val ach = achievements[index]
                AchievementCard(
                    ach = ach,
                    isMale = isMale,
                    card = card,
                    border = border,
                    titleColor = titleColor,
                    subColor = subColor,
                    pulseScale = if (ach.isUnlocked) pulseScale else 1f,
                    shimmerAlpha = if (ach.isUnlocked) shimmer else 1f
                )
            }
            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun AchievementCard(
    ach: AchievementData,
    isMale: Boolean,
    card: Color,
    border: Color,
    titleColor: Color,
    subColor: Color,
    pulseScale: Float,
    shimmerAlpha: Float
) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .background(
                if (ach.isUnlocked)
                    Brush.horizontalGradient(
                        listOf(ach.unlockedColor.copy(0.18f), ach.unlockedColor.copy(0.06f))
                    )
                else Brush.horizontalGradient(listOf(card, card)),
                RoundedCornerShape(18.dp)
            )
            .border(
                if (ach.isUnlocked) 2.dp else 1.dp,
                if (ach.isUnlocked) ach.unlockedColor.copy(0.7f) else border,
                RoundedCornerShape(18.dp)
            )
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Badge circle
            Box(
                modifier = Modifier.size(64.dp)
                    .scale(if (ach.isUnlocked) pulseScale else 1f)
                    .background(
                        if (ach.isUnlocked)
                            Brush.radialGradient(listOf(ach.unlockedColor.copy(0.3f), ach.unlockedColor.copy(0.1f)))
                        else Brush.radialGradient(listOf(Color(0xFF1A1840), Color(0xFF0D0B28))),
                        CircleShape
                    )
                    .border(
                        if (ach.isUnlocked) 2.dp else 1.dp,
                        if (ach.isUnlocked) ach.unlockedColor.copy(shimmerAlpha) else border,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (ach.isUnlocked) {
                    Text(ach.emoji, fontSize = 28.sp,
                        modifier = Modifier.graphicsLayer { alpha = shimmerAlpha * 0.5f + 0.5f })
                } else {
                    Icon(Icons.Default.Lock, null, tint = if (isMale) TextHint else subColor, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        ach.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (ach.isUnlocked) ach.unlockedColor else titleColor
                    )
                    if (ach.isUnlocked) {
                        Spacer(Modifier.width(6.dp))
                        Text("✓", fontSize = 13.sp, color = ach.unlockedColor, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(ach.description, fontSize = 12.sp, color = subColor, lineHeight = 16.sp)
                Spacer(Modifier.height(8.dp))
                // Progress bar
                val progress = ach.progress.toFloat() / ach.total.toFloat()
                Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(if (isMale) Color(0xFF1A1840) else Color(0xFFFFE4EF), RoundedCornerShape(3.dp))) {
                    Box(
                        modifier = Modifier.fillMaxWidth(progress).height(6.dp)
                            .background(if (ach.isUnlocked) ach.unlockedColor else ach.unlockedColor.copy(0.5f), RoundedCornerShape(3.dp))
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "${ach.progress} / ${ach.total}",
                    fontSize = 11.sp,
                    color = if (ach.isUnlocked) ach.unlockedColor else subColor,
                    fontWeight = if (ach.isUnlocked) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}
