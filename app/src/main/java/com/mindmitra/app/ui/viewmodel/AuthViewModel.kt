package com.mindmitra.app.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("mindmitra_auth", 0)

    var isLoggedIn by mutableStateOf(prefs.getBoolean("is_logged_in", false))
        private set

    var userGender by mutableStateOf(prefs.getString("gender", "") ?: "")
        private set

    /** true  → boy dark-theme app
     *  false → girl pink-theme app */
    val isMale: Boolean get() = userGender.equals("Male", ignoreCase = true)

    fun login(email: String, password: String): Boolean {
        val storedEmail = prefs.getString("email", "") ?: ""
        val storedPwd   = prefs.getString("password", "") ?: ""
        return if (email.isNotBlank() && email == storedEmail && password == storedPwd) {
            isLoggedIn = true
            prefs.edit().putBoolean("is_logged_in", true).apply()
            true
        } else false
    }

    fun signup(name: String, email: String, password: String, gender: String) {
        userGender = gender
        isLoggedIn = true
        prefs.edit()
            .putString("display_name", name.trim())
            .putString("email", email.trim())
            .putString("password", password)
            .putString("gender", gender)
            .putBoolean("is_logged_in", true)
            .apply()
    }

    fun storedDisplayName(): String =
        prefs.getString("display_name", "Jyoti") ?: "Jyoti"

    val storedEmail: String get() = prefs.getString("email", "") ?: ""
    val storedGender: String get() = prefs.getString("gender", "") ?: ""

    fun logout() {
        isLoggedIn = false
        prefs.edit().putBoolean("is_logged_in", false).apply()
    }
}
