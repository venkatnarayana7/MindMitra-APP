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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import com.mindmitra.app.ui.theme.GirlBg
import com.mindmitra.app.ui.theme.GirlBorder
import com.mindmitra.app.ui.theme.GirlCard
import com.mindmitra.app.ui.theme.GirlPrimary
import com.mindmitra.app.ui.theme.GirlPrimaryDim
import com.mindmitra.app.ui.theme.GirlPrimaryLight
import com.mindmitra.app.ui.theme.GirlTextDark
import com.mindmitra.app.ui.theme.GirlTextLight
import com.mindmitra.app.ui.theme.GirlTextMid
import com.mindmitra.app.ui.viewmodel.UserViewModel

@Composable
fun GirlProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    Scaffold(
        bottomBar = { GirlBottomBar(navController = navController, selectedIndex = 4) },
        containerColor = GirlBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(GirlBg),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item { GirlProfileHeader(navController = navController) }
            item { GirlProfileCard(userViewModel = userViewModel) }
            item { Spacer(modifier = Modifier.height(20.dp)) }
            item { GirlProfileMenuSection(navController = navController, userViewModel = userViewModel) }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun GirlProfileHeader(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Profile", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GirlTextDark)
        Box(
            modifier = Modifier.size(38.dp).background(GirlCard, CircleShape).border(1.dp, GirlBorder, CircleShape).clickable { navController.navigate(Routes.GIRL_SETTINGS) },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Settings, "Settings", tint = GirlTextMid, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun GirlProfileCard(userViewModel: UserViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GirlCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GirlBorder)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            // Female avatar
            Box(
                modifier = Modifier.size(72.dp).background(brush = Brush.radialGradient(colors = listOf(GirlPrimaryLight, GirlPrimary)), shape = CircleShape).border(2.dp, GirlBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(52.dp)) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    // Hair
                    drawArc(
                        brush = Brush.radialGradient(colors = listOf(Color(0xFF4A2020), Color(0xFF2A1010))),
                        startAngle = 180f, sweepAngle = 180f, useCenter = true,
                        topLeft = Offset(cx - 16f, cy - 22f), size = Size(32f, 26f)
                    )
                    // Side hair
                    drawOval(color = Color(0xFF4A2020), topLeft = Offset(cx - 17f, cy - 14f), size = Size(8f, 20f))
                    drawOval(color = Color(0xFF4A2020), topLeft = Offset(cx + 9f, cy - 14f), size = Size(8f, 20f))
                    // Face
                    drawCircle(color = Color(0xFFFFD5B0), radius = 14f, center = Offset(cx, cy - 4f))
                    // Eyes
                    drawCircle(color = Color(0xFF3A2A1A), radius = 2.2f, center = Offset(cx - 4.5f, cy - 6f))
                    drawCircle(color = Color(0xFF3A2A1A), radius = 2.2f, center = Offset(cx + 4.5f, cy - 6f))
                    // Lashes
                    drawLine(Color(0xFF3A2A1A), Offset(cx - 6f, cy - 8f), Offset(cx - 4.5f, cy - 8.5f), strokeWidth = 1.2f)
                    drawLine(Color(0xFF3A2A1A), Offset(cx + 3f, cy - 8f), Offset(cx + 4.5f, cy - 8.5f), strokeWidth = 1.2f)
                    // Smile
                    drawArc(
                        color = Color(0xFFB04030),
                        startAngle = 15f, sweepAngle = 150f, useCenter = false,
                        topLeft = Offset(cx - 5f, cy - 3f), size = Size(10f, 6f),
                        style = Stroke(width = 1.8f, cap = StrokeCap.Round)
                    )
                    // Blush
                    drawCircle(Color(0xFFFF80AB).copy(0.35f), 5f, Offset(cx - 9f, cy - 2f))
                    drawCircle(Color(0xFFFF80AB).copy(0.35f), 5f, Offset(cx + 9f, cy - 2f))
                    // Body
                    drawArc(
                        brush = Brush.radialGradient(colors = listOf(GirlPrimaryLight, GirlPrimary)),
                        startAngle = 0f, sweepAngle = 180f, useCenter = true,
                        topLeft = Offset(cx - 18f, cy + 8f), size = Size(36f, 22f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(userViewModel.userName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GirlTextDark)
                Spacer(modifier = Modifier.height(2.dp))
                Text("You are amazing! 💗", fontSize = 13.sp, color = GirlTextMid)
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier.background(GirlPrimaryDim, RoundedCornerShape(20.dp)).border(1.dp, GirlBorder, RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(userViewModel.badgeTitle, fontSize = 11.sp, color = GirlPrimary, fontWeight = FontWeight.SemiBold)
                    }
                    Box(
                        modifier = Modifier.background(GirlCard, RoundedCornerShape(20.dp)).border(1.dp, GirlBorder, RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Level ${userViewModel.userLevel} ›", fontSize = 11.sp, color = GirlTextMid, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun GirlProfileMenuSection(navController: NavController, userViewModel: UserViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GirlCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GirlBorder)
    ) {
        Column {
            GirlMenuRow(Icons.Default.MenuBook, GirlPrimaryDim, GirlPrimary, "My Journal", "${userViewModel.journalCount} Entries", true) {}
            GirlMenuRow(Icons.Default.EmojiEvents, Color(0xFFFFF3E0), Color(0xFFFF9800), "Achievements", "${userViewModel.badgeCount} Badges", true) { navController.navigate(Routes.ACHIEVEMENTS) }
            GirlMenuToggleRow(Icons.Default.Notifications, Color(0xFFE8F5E9), Color(0xFF4CAF50), "Reminders", userViewModel.remindersEnabled, { userViewModel.updateRemindersEnabled(it) }, true)
            GirlMenuRow(Icons.Default.Bookmark, Color(0xFFE3F2FD), Color(0xFF2196F3), "Saved", "Moments: Journey", true) {}
            GirlMenuRow(Icons.Default.Headset, GirlPrimaryDim, GirlPrimary, "Help & Support", "Here to help 💗", false) { navController.navigate(Routes.GIRL_SETTINGS) }
        }
    }
}

@Composable
private fun GirlMenuRow(icon: ImageVector, iconBg: Color, iconTint: Color, label: String, value: String, showDivider: Boolean, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(40.dp).background(iconBg, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, label, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = GirlTextDark, modifier = Modifier.weight(1f))
            Text(value, fontSize = 12.sp, color = GirlTextMid)
            Spacer(modifier = Modifier.width(4.dp))
            Text("›", fontSize = 16.sp, color = GirlTextLight, fontWeight = FontWeight.Light)
        }
        if (showDivider) Divider(modifier = Modifier.padding(horizontal = 16.dp), color = GirlBorder, thickness = 0.5.dp)
    }
}

@Composable
private fun GirlMenuToggleRow(icon: ImageVector, iconBg: Color, iconTint: Color, label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, showDivider: Boolean) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(40.dp).background(iconBg, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, label, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = GirlTextDark, modifier = Modifier.weight(1f))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = GirlPrimary,
                    uncheckedThumbColor = GirlTextLight,
                    uncheckedTrackColor = Color(0xFFFFD0E4)
                )
            )
        }
        if (showDivider) Divider(modifier = Modifier.padding(horizontal = 16.dp), color = GirlBorder, thickness = 0.5.dp)
    }
}
