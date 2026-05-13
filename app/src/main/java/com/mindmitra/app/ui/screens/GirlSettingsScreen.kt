package com.mindmitra.app.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
fun GirlSettingsScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    var nameEditValue by remember { mutableStateOf(userViewModel.userName) }
    var isEditingName by remember { mutableStateOf(false) }
    var showTimeDropdown by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val reminderTimes = listOf("6:00 AM", "7:00 AM", "8:00 AM", "9:00 AM",
        "12:00 PM", "3:00 PM", "6:00 PM", "9:00 PM")
    val languages = listOf("English", "Hindi", "Hinglish")

    LaunchedEffect(userViewModel.userName) {
        if (!isEditingName) nameEditValue = userViewModel.userName
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GirlBg)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(GirlCard, CircleShape)
                    .border(1.dp, GirlBorder, CircleShape)
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = GirlPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GirlTextDark
            )
        }

        // ── Settings list ─────────────────────────────────────────────────────
        LazyColumn(
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            // ── PROFILE ───────────────────────────────────────────────────────
            item { GirlSectionLabel("PROFILE") }
            item {
                GirlSettingsCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GirlIconBox(Icons.Default.AccountCircle, GirlPrimary, GirlPrimaryDim)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Display Name", fontSize = 13.sp, color = GirlTextMid)
                            Spacer(modifier = Modifier.height(4.dp))
                            if (isEditingName) {
                                BasicTextField(
                                    value = nameEditValue,
                                    onValueChange = { nameEditValue = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(focusRequester),
                                    textStyle = TextStyle(
                                        color = GirlTextDark,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    cursorBrush = SolidColor(GirlPrimary),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = {
                                        userViewModel.updateUserName(nameEditValue)
                                        isEditingName = false
                                        focusManager.clearFocus()
                                    })
                                )
                                LaunchedEffect(Unit) { focusRequester.requestFocus() }
                            } else {
                                Text(
                                    text = userViewModel.userName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = GirlTextDark
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        if (isEditingName) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(GirlPrimary, CircleShape)
                                    .clickable {
                                        userViewModel.updateUserName(nameEditValue)
                                        isEditingName = false
                                        focusManager.clearFocus()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(GirlCard, CircleShape)
                                    .border(1.dp, GirlBorder, CircleShape)
                                    .clickable { isEditingName = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Edit, null, tint = GirlTextMid, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // ── NOTIFICATIONS ─────────────────────────────────────────────────
            item { GirlSectionLabel("NOTIFICATIONS") }
            item {
                GirlSettingsCard {
                    GirlToggleRow(
                        icon = Icons.Default.Notifications,
                        iconTint = Color(0xFFFF7043),
                        iconBg = Color(0xFFFFEDE8),
                        title = "Push Notifications",
                        subtitle = "Get important updates & reminders",
                        checked = userViewModel.notificationsEnabled,
                        onCheckedChange = { userViewModel.updateNotificationsEnabled(it) }
                    )
                    GirlSettingsDivider()
                    GirlToggleRow(
                        icon = Icons.Default.Timer,
                        iconTint = Color(0xFF2E7D32),
                        iconBg = Color(0xFFE8F5E9),
                        title = "Daily Reminders",
                        subtitle = "Remind me to check in",
                        checked = userViewModel.remindersEnabled,
                        onCheckedChange = { userViewModel.updateRemindersEnabled(it) }
                    )
                    if (userViewModel.remindersEnabled) {
                        GirlSettingsDivider()
                        Box {
                            GirlValueRow(
                                icon = Icons.Default.Schedule,
                                iconTint = GirlPrimary,
                                iconBg = GirlPrimaryDim,
                                title = "Reminder Time",
                                value = userViewModel.reminderTime,
                                onClick = { showTimeDropdown = true }
                            )
                            DropdownMenu(
                                expanded = showTimeDropdown,
                                onDismissRequest = { showTimeDropdown = false },
                                modifier = Modifier.background(GirlCard)
                            ) {
                                reminderTimes.forEach { time ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = time,
                                                color = if (time == userViewModel.reminderTime)
                                                    GirlPrimary else GirlTextDark
                                            )
                                        },
                                        onClick = {
                                            userViewModel.updateReminderTime(time)
                                            showTimeDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── PREFERENCES ───────────────────────────────────────────────────
            item { GirlSectionLabel("PREFERENCES") }
            item {
                GirlSettingsCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GirlIconBox(Icons.Default.Language, Color(0xFF1565C0), Color(0xFFE3F2FD))
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Language", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = GirlTextDark)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                languages.forEach { lang ->
                                    val selected = lang == userViewModel.language
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (selected) GirlPrimary else GirlSurface,
                                                RoundedCornerShape(20.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (selected) GirlPrimary else GirlBorder,
                                                RoundedCornerShape(20.dp)
                                            )
                                            .clickable { userViewModel.updateLanguage(lang) }
                                            .padding(horizontal = 12.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = lang,
                                            fontSize = 12.sp,
                                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                            color = if (selected) Color.White else GirlTextMid
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── PRIVACY & DATA ────────────────────────────────────────────────
            item { GirlSectionLabel("PRIVACY & DATA") }
            item {
                GirlSettingsCard {
                    GirlToggleRow(
                        icon = Icons.Default.Shield,
                        iconTint = GirlPrimary,
                        iconBg = GirlPrimaryDim,
                        title = "Share Analytics",
                        subtitle = "Help improve MindMitra (anonymous)",
                        checked = userViewModel.analyticsEnabled,
                        onCheckedChange = { userViewModel.updateAnalyticsEnabled(it) }
                    )
                }
            }

            // ── ABOUT ─────────────────────────────────────────────────────────
            item { GirlSectionLabel("ABOUT") }
            item {
                GirlSettingsCard {
                    GirlInfoRow(
                        icon = Icons.Default.Info,
                        iconTint = Color(0xFF1565C0),
                        iconBg = Color(0xFFE3F2FD),
                        title = "App Version",
                        value = "1.0.0"
                    )
                    GirlSettingsDivider()
                    GirlActionRow(
                        icon = Icons.Default.Policy,
                        iconTint = GirlPrimary,
                        iconBg = GirlPrimaryDim,
                        title = "Terms of Service"
                    )
                    GirlSettingsDivider()
                    GirlActionRow(
                        icon = Icons.Default.Shield,
                        iconTint = Color(0xFF2E7D32),
                        iconBg = Color(0xFFE8F5E9),
                        title = "Privacy Policy"
                    )
                    GirlSettingsDivider()
                    GirlActionRow(
                        icon = Icons.Default.Star,
                        iconTint = Color(0xFFFFD700),
                        iconBg = Color(0xFFFFFDE7),
                        title = "Rate MindMitra 💗"
                    )
                    GirlSettingsDivider()
                    GirlActionRow(
                        icon = Icons.Default.Headset,
                        iconTint = Color(0xFF6A1B9A),
                        iconBg = Color(0xFFF3E5F5),
                        title = "Help & Support"
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// ─── Reusable row components ─────────────────────────────────────────────────

@Composable
private fun GirlSectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = GirlPrimary,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 6.dp)
    )
}

@Composable
private fun GirlSettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GirlCard),
        border = BorderStroke(1.dp, GirlBorder)
    ) {
        Column { content() }
    }
}

@Composable
private fun GirlIconBox(icon: ImageVector, tint: Color, bg: Color) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(bg, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun GirlSettingsDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = GirlBorder,
        thickness = 0.5.dp
    )
}

@Composable
private fun GirlToggleRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GirlIconBox(icon, iconTint, iconBg)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = GirlTextDark)
            Text(subtitle, fontSize = 11.sp, color = GirlTextMid)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GirlPrimary,
                uncheckedThumbColor = GirlTextLight,
                uncheckedTrackColor = GirlSurface
            )
        )
    }
}

@Composable
private fun GirlValueRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GirlIconBox(icon, iconTint, iconBg)
        Spacer(modifier = Modifier.width(14.dp))
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = GirlTextDark, modifier = Modifier.weight(1f))
        Text(value, fontSize = 13.sp, color = GirlPrimary, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(Icons.Default.ChevronRight, null, tint = GirlTextLight, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun GirlInfoRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GirlIconBox(icon, iconTint, iconBg)
        Spacer(modifier = Modifier.width(14.dp))
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = GirlTextDark, modifier = Modifier.weight(1f))
        Text(value, fontSize = 13.sp, color = GirlTextMid)
    }
}

@Composable
private fun GirlActionRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GirlIconBox(icon, iconTint, iconBg)
        Spacer(modifier = Modifier.width(14.dp))
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = GirlTextDark, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, null, tint = GirlTextLight, modifier = Modifier.size(18.dp))
    }
}
