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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mindmitra.app.navigation.Routes
import com.mindmitra.app.ui.theme.AccentLavender
import com.mindmitra.app.ui.theme.BlueAccent
import com.mindmitra.app.ui.theme.BottomNavBg
import com.mindmitra.app.ui.theme.CardSurface
import com.mindmitra.app.ui.theme.DeepNavy
import com.mindmitra.app.ui.theme.GreenTeal
import com.mindmitra.app.ui.theme.OrangeAccent
import com.mindmitra.app.ui.theme.PrimaryPurple
import com.mindmitra.app.ui.theme.TextHint
import com.mindmitra.app.ui.theme.TextPrimary
import com.mindmitra.app.ui.theme.TextSecondary
import com.mindmitra.app.ui.viewmodel.UserViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    Scaffold(
        bottomBar = { ProfileBottomBar(navController = navController) },
        containerColor = DeepNavy
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DeepNavy),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item { ProfileHeader(navController = navController) }
            item { ProfileCard(userViewModel = userViewModel) }
            item { Spacer(modifier = Modifier.height(20.dp)) }
            item { ProfileMenuSection(navController = navController, userViewModel = userViewModel) }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

// ─── Header ──────────────────────────────────────────────────────────────────

@Composable
private fun ProfileHeader(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Profile",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Box(
            modifier = Modifier
                .size(38.dp)
                .background(CardSurface, CircleShape)
                .border(1.dp, PrimaryPurple.copy(alpha = 0.3f), CircleShape)
                .clickable { navController.navigate(Routes.SETTINGS) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ─── Profile card ─────────────────────────────────────────────────────────────

@Composable
private fun ProfileCard(userViewModel: UserViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF5A52A8), Color(0xFF2D2060))
                        ),
                        shape = CircleShape
                    )
                    .border(2.dp, AccentLavender.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(52.dp)) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    drawArc(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFB39DDB), Color(0xFF7C5CBF))
                        ),
                        startAngle = 180f, sweepAngle = 180f, useCenter = true,
                        topLeft = Offset(cx - 14f, cy - 20f), size = Size(28f, 24f)
                    )
                    drawCircle(color = Color(0xFFFFD5B0), radius = 13f, center = Offset(cx, cy - 6f))
                    drawCircle(color = Color(0xFF3A2A1A), radius = 2.2f, center = Offset(cx - 4.5f, cy - 7f))
                    drawCircle(color = Color(0xFF3A2A1A), radius = 2.2f, center = Offset(cx + 4.5f, cy - 7f))
                    drawArc(
                        color = Color(0xFFB04030),
                        startAngle = 15f, sweepAngle = 150f, useCenter = false,
                        topLeft = Offset(cx - 5f, cy - 4f), size = Size(10f, 6f),
                        style = Stroke(width = 1.8f, cap = StrokeCap.Round)
                    )
                    drawArc(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF5A52A8), Color(0xFF3A3480))
                        ),
                        startAngle = 0f, sweepAngle = 180f, useCenter = true,
                        topLeft = Offset(cx - 18f, cy + 6f), size = Size(36f, 22f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userViewModel.userName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Keep going, star ⭐",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Badge title from ViewModel (updates with level)
                    Box(
                        modifier = Modifier
                            .background(PrimaryPurple.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                            .border(1.dp, AccentLavender.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = userViewModel.badgeTitle,
                            fontSize = 11.sp,
                            color = AccentLavender,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(CardSurface, RoundedCornerShape(20.dp))
                            .border(1.dp, PrimaryPurple.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Level ${userViewModel.userLevel} ›",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// ─── Menu section ─────────────────────────────────────────────────────────────

@Composable
private fun ProfileMenuSection(
    navController: NavController,
    userViewModel: UserViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.18f))
    ) {
        Column {
            MenuRow(
                icon = Icons.Default.MenuBook,
                iconBg = Color(0xFF1E1040),
                iconTint = AccentLavender,
                label = "My Journal",
                value = "${userViewModel.journalCount} Entries",
                showDivider = true,
                onClick = {}
            )
            MenuRow(
                icon = Icons.Default.EmojiEvents,
                iconBg = Color(0xFF2A1800),
                iconTint = OrangeAccent,
                label = "Achievements",
                value = "${userViewModel.badgeCount} Badges",
                showDivider = true,
                onClick = { navController.navigate(Routes.ACHIEVEMENTS) }
            )
            MenuToggleRow(
                icon = Icons.Default.Notifications,
                iconBg = Color(0xFF0D2B1A),
                iconTint = GreenTeal,
                label = "Reminders",
                checked = userViewModel.remindersEnabled,
                onCheckedChange = { userViewModel.updateRemindersEnabled(it) },
                showDivider = true
            )
            MenuRow(
                icon = Icons.Default.Bookmark,
                iconBg = Color(0xFF0D1F38),
                iconTint = BlueAccent,
                label = "Saved",
                value = "Moments: Journey",
                showDivider = true,
                onClick = {}
            )
            MenuRow(
                icon = Icons.Default.Headset,
                iconBg = Color(0xFF1E1040),
                iconTint = AccentLavender,
                label = "Help & Support",
                value = "Here to help",
                showDivider = false,
                onClick = { navController.navigate(Routes.SETTINGS) }
            )
        }
    }
}

@Composable
private fun MenuRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    value: String,
    showDivider: Boolean,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, label, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(text = value, fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "›", fontSize = 16.sp, color = TextHint, fontWeight = FontWeight.Light)
        }
        if (showDivider) {
            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = PrimaryPurple.copy(alpha = 0.1f),
                thickness = 0.5.dp
            )
        }
    }
}

@Composable
private fun MenuToggleRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, label, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryPurple,
                    uncheckedThumbColor = TextHint,
                    uncheckedTrackColor = Color(0xFF2A2850)
                )
            )
        }
        if (showDivider) {
            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = PrimaryPurple.copy(alpha = 0.1f),
                thickness = 0.5.dp
            )
        }
    }
}

// ─── Bottom navigation ────────────────────────────────────────────────────────

@Composable
private fun ProfileBottomBar(navController: NavController) {
    val items = listOf(
        Triple("Home",     Icons.Default.Home,     Routes.HOME),
        Triple("Chat",     Icons.Default.Chat,     Routes.AI_CHAT),
        Triple("Journal",  Icons.Default.Book,     Routes.JOURNAL),
        Triple("Com",      Icons.Default.Group,    Routes.COMMUNITY),
        Triple("Profile",  Icons.Default.Person,   Routes.PROFILE),
    )
    NavigationBar(containerColor = BottomNavBg, tonalElevation = 0.dp) {
        items.forEachIndexed { index, (label, icon, route) ->
            val isSelected = index == 4
            NavigationBarItem(
                icon = { Icon(icon, label, modifier = Modifier.size(22.dp)) },
                label = {
                    Text(label, fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                },
                selected = isSelected,
                onClick = {
                    if (!isSelected) navController.navigate(route) {
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
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
