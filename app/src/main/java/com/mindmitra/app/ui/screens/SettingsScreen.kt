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
import androidx.compose.material.icons.filled.Analytics
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
import com.mindmitra.app.ui.theme.AccentLavender
import com.mindmitra.app.ui.theme.BlueAccent
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
fun SettingsScreen(
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

    // Keep local edit value in sync when userName changes
    LaunchedEffect(userViewModel.userName) {
        if (!isEditingName) nameEditValue = userViewModel.userName
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
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
                    .background(CardSurface, CircleShape)
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // ── Settings list ─────────────────────────────────────────────────────
        LazyColumn(
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            // ── PROFILE ───────────────────────────────────────────────────────
            item { SectionLabel("PROFILE") }
            item {
                SettingsCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconBox(Icons.Default.AccountCircle, AccentLavender, Color(0xFF1A1040))
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Display Name", fontSize = 13.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            if (isEditingName) {
                                BasicTextField(
                                    value = nameEditValue,
                                    onValueChange = { nameEditValue = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(focusRequester),
                                    textStyle = TextStyle(
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    cursorBrush = SolidColor(AccentLavender),
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
                                    color = TextPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        if (isEditingName) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(PrimaryPurple, CircleShape)
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
                                    .background(CardSurface, CircleShape)
                                    .border(1.dp, PrimaryPurple.copy(0.3f), CircleShape)
                                    .clickable { isEditingName = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Edit, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // ── NOTIFICATIONS ─────────────────────────────────────────────────
            item { SectionLabel("NOTIFICATIONS") }
            item {
                SettingsCard {
                    ToggleRow(
                        icon = Icons.Default.Notifications,
                        iconTint = OrangeAccent,
                        iconBg = Color(0xFF2A1A00),
                        title = "Push Notifications",
                        subtitle = "Get important updates & reminders",
                        checked = userViewModel.notificationsEnabled,
                        onCheckedChange = { userViewModel.updateNotificationsEnabled(it) }
                    )
                    SettingsDivider()
                    ToggleRow(
                        icon = Icons.Default.Timer,
                        iconTint = GreenTeal,
                        iconBg = Color(0xFF0D2B1A),
                        title = "Daily Reminders",
                        subtitle = "Remind me to check in",
                        checked = userViewModel.remindersEnabled,
                        onCheckedChange = { userViewModel.updateRemindersEnabled(it) }
                    )
                    if (userViewModel.remindersEnabled) {
                        SettingsDivider()
                        Box {
                            ValueRow(
                                icon = Icons.Default.Schedule,
                                iconTint = AccentLavender,
                                iconBg = Color(0xFF1A1040),
                                title = "Reminder Time",
                                value = userViewModel.reminderTime,
                                onClick = { showTimeDropdown = true }
                            )
                            DropdownMenu(
                                expanded = showTimeDropdown,
                                onDismissRequest = { showTimeDropdown = false },
                                modifier = Modifier.background(Color(0xFF1E1B3C))
                            ) {
                                reminderTimes.forEach { time ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = time,
                                                color = if (time == userViewModel.reminderTime)
                                                    AccentLavender else TextPrimary
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
            item { SectionLabel("PREFERENCES") }
            item {
                SettingsCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconBox(Icons.Default.Language, BlueAccent, Color(0xFF0D1E38))
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Language", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                languages.forEach { lang ->
                                    val selected = lang == userViewModel.language
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (selected) PrimaryPurple else Color(0xFF12103A),
                                                RoundedCornerShape(20.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (selected) AccentLavender else PrimaryPurple.copy(0.28f),
                                                RoundedCornerShape(20.dp)
                                            )
                                            .clickable { userViewModel.updateLanguage(lang) }
                                            .padding(horizontal = 12.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = lang,
                                            fontSize = 12.sp,
                                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                            color = if (selected) Color.White else TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── PRIVACY & DATA ────────────────────────────────────────────────
            item { SectionLabel("PRIVACY & DATA") }
            item {
                SettingsCard {
                    ToggleRow(
                        icon = Icons.Default.Shield,
                        iconTint = Color(0xFFFF8FAB),
                        iconBg = Color(0xFF2A0D18),
                        title = "Share Analytics",
                        subtitle = "Help improve MindMitra (anonymous)",
                        checked = userViewModel.analyticsEnabled,
                        onCheckedChange = { userViewModel.updateAnalyticsEnabled(it) }
                    )
                }
            }

            // ── ABOUT ─────────────────────────────────────────────────────────
            item { SectionLabel("ABOUT") }
            item {
                SettingsCard {
                    InfoRow(
                        icon = Icons.Default.Info,
                        iconTint = BlueAccent,
                        iconBg = Color(0xFF0D1E38),
                        title = "App Version",
                        value = "1.0.0"
                    )
                    SettingsDivider()
                    ActionRow(
                        icon = Icons.Default.Policy,
                        iconTint = AccentLavender,
                        iconBg = Color(0xFF1A1040),
                        title = "Terms of Service"
                    )
                    SettingsDivider()
                    ActionRow(
                        icon = Icons.Default.Shield,
                        iconTint = GreenTeal,
                        iconBg = Color(0xFF0D2B1A),
                        title = "Privacy Policy"
                    )
                    SettingsDivider()
                    ActionRow(
                        icon = Icons.Default.Star,
                        iconTint = Color(0xFFFFD700),
                        iconBg = Color(0xFF2A2000),
                        title = "Rate MindMitra ⭐"
                    )
                    SettingsDivider()
                    ActionRow(
                        icon = Icons.Default.Headset,
                        iconTint = Color(0xFF7BBFFF),
                        iconBg = Color(0xFF0D1E38),
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
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextHint,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 6.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.18f))
    ) {
        Column { content() }
    }
}

@Composable
private fun IconBox(icon: ImageVector, tint: Color, bg: Color) {
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
private fun SettingsDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = PrimaryPurple.copy(alpha = 0.1f),
        thickness = 0.5.dp
    )
}

@Composable
private fun ToggleRow(
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
        IconBox(icon, iconTint, iconBg)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text(subtitle, fontSize = 11.sp, color = TextSecondary)
        }
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
}

@Composable
private fun ValueRow(
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
        IconBox(icon, iconTint, iconBg)
        Spacer(modifier = Modifier.width(14.dp))
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary, modifier = Modifier.weight(1f))
        Text(value, fontSize = 13.sp, color = AccentLavender, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(Icons.Default.ChevronRight, null, tint = TextHint, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun InfoRow(
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
        IconBox(icon, iconTint, iconBg)
        Spacer(modifier = Modifier.width(14.dp))
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary, modifier = Modifier.weight(1f))
        Text(value, fontSize = 13.sp, color = TextSecondary)
    }
}

@Composable
private fun ActionRow(
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
        IconBox(icon, iconTint, iconBg)
        Spacer(modifier = Modifier.width(14.dp))
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, null, tint = TextHint, modifier = Modifier.size(18.dp))
    }
}
