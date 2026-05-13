package com.mindmitra.app.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class JournalEntryData(
    val dateTime: String,
    val title: String,
    val content: String,
    val mood: String      // emoji
)

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("mindmitra_prefs", 0)

    // ── Profile ───────────────────────────────────────────────────────────────
    var userName by mutableStateOf(prefs.getString("user_name", "Jyoti") ?: "Jyoti")
        private set

    var journalCount by mutableIntStateOf(prefs.getInt("journal_count", 5))
        private set

    var badgeCount by mutableIntStateOf(prefs.getInt("badge_count", 4))
        private set

    val userLevel: Int get() = when {
        journalCount >= 50 -> 5
        journalCount >= 30 -> 4
        journalCount >= 15 -> 3
        journalCount >= 6  -> 2
        else               -> 1
    }

    val badgeTitle: String get() = when (userLevel) {
        5    -> "Mind Master"
        4    -> "Wellness Pro"
        3    -> "Progress Star"
        2    -> "Mood Explorer"
        else -> "Mood Warrior"
    }

    // ── Notifications ─────────────────────────────────────────────────────────
    var notificationsEnabled by mutableStateOf(prefs.getBoolean("notifications_enabled", true))
        private set

    var remindersEnabled by mutableStateOf(prefs.getBoolean("reminders_enabled", true))
        private set

    var reminderTime by mutableStateOf(prefs.getString("reminder_time", "9:00 AM") ?: "9:00 AM")
        private set

    // ── Preferences ───────────────────────────────────────────────────────────
    var language by mutableStateOf(prefs.getString("language", "English") ?: "English")
        private set

    // ── Privacy ───────────────────────────────────────────────────────────────
    var analyticsEnabled by mutableStateOf(prefs.getBoolean("analytics_enabled", true))
        private set

    // ── Mutators ──────────────────────────────────────────────────────────────
    fun updateUserName(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return
        userName = trimmed
        prefs.edit().putString("user_name", trimmed).apply()
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        notificationsEnabled = enabled
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    fun updateRemindersEnabled(enabled: Boolean) {
        remindersEnabled = enabled
        prefs.edit().putBoolean("reminders_enabled", enabled).apply()
    }

    fun updateReminderTime(time: String) {
        reminderTime = time
        prefs.edit().putString("reminder_time", time).apply()
    }

    fun updateLanguage(lang: String) {
        language = lang
        prefs.edit().putString("language", lang).apply()
    }

    fun updateAnalyticsEnabled(enabled: Boolean) {
        analyticsEnabled = enabled
        prefs.edit().putBoolean("analytics_enabled", enabled).apply()
    }

    // ── Journal entries (in-memory, survive navigation, reset on restart) ────────
    val journalEntries = androidx.compose.runtime.mutableStateListOf<JournalEntryData>()

    fun addJournalEntry(title: String, content: String, mood: String) {
        val fmt = SimpleDateFormat("MMM dd  •  hh:mm a", Locale.getDefault())
        journalEntries.add(0, JournalEntryData(fmt.format(Date()), title.trim(), content.trim(), mood))
        journalCount++
        prefs.edit().putInt("journal_count", journalCount).apply()
    }

    fun incrementJournalCount() {
        journalCount++
        prefs.edit().putInt("journal_count", journalCount).apply()
    }

    // ── Streak (real date-based) ───────────────────────────────────────────────
    var streakCount by mutableIntStateOf(prefs.getInt("streak_count", 0))
        private set

    /** Call once when the home screen appears. Compares today vs last open date. */
    fun checkAndUpdateStreak() {
        val fmt       = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val today     = fmt.format(Date())
        val lastDate  = prefs.getString("last_open_date", "") ?: ""
        val cal       = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val yesterday = fmt.format(cal.time)

        // Record this date in the open-dates set (for streak map)
        val openSet = prefs.getStringSet("open_dates", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        openSet.add(today)
        when {
            lastDate == today -> {
                prefs.edit().putStringSet("open_dates", openSet).apply()
            }
            lastDate == yesterday -> {
                streakCount++
                prefs.edit().putInt("streak_count", streakCount)
                    .putString("last_open_date", today)
                    .putStringSet("open_dates", openSet).apply()
            }
            else -> {
                streakCount = 1
                prefs.edit().putInt("streak_count", 1)
                    .putString("last_open_date", today)
                    .putStringSet("open_dates", openSet).apply()
            }
        }
    }

    // ── Breathing achievement ─────────────────────────────────────────────────
    var breathingSessionsCompleted by mutableIntStateOf(prefs.getInt("breathing_sessions", 0))
        private set

    /** True once user has completed ≥ 10 full breathing cycles. */
    val isBreathAchievementUnlocked: Boolean get() = breathingSessionsCompleted >= 10

    /** Returns all dates (yyyyMMdd strings) the user opened the app. */
    fun getOpenDates(): Set<String> =
        prefs.getStringSet("open_dates", emptySet()) ?: emptySet()

    fun incrementBreathingSession() {
        breathingSessionsCompleted++
        prefs.edit().putInt("breathing_sessions", breathingSessionsCompleted).apply()
    }
}
