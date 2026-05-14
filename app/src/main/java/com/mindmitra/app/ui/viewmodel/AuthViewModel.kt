package com.mindmitra.app.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mindmitra.app.data.auth.AuthApi
import com.mindmitra.app.data.auth.UserProfile
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("mindmitra_auth", 0)

    var isLoggedIn by mutableStateOf(prefs.getBoolean("is_logged_in", false))
        private set

    var userGender by mutableStateOf(prefs.getString("gender", "") ?: "")
        private set

    var isLoading by mutableStateOf(false)
        private set

    /** true → boy dark-theme app, false → girl pink-theme app */
    val isMale: Boolean get() = userGender.equals("Male", ignoreCase = true)

    fun login(
        email: String,
        password: String,
        onSuccess: (isMale: Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        if (isLoading) return
        viewModelScope.launch {
            isLoading = true
            AuthApi.login(email.trim(), password)
                .onSuccess { user ->
                    persistUser(user)
                    isLoading = false
                    onSuccess(user.gender.equals("Male", ignoreCase = true))
                }
                .onFailure { err ->
                    isLoading = false
                    onError(err.message ?: "Login failed. Please try again.")
                }
        }
    }

    fun signup(
        name: String,
        email: String,
        password: String,
        gender: String,
        onSuccess: (isMale: Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        if (isLoading) return
        viewModelScope.launch {
            isLoading = true
            AuthApi.signup(name.trim(), email.trim(), password, gender)
                .onSuccess { user ->
                    persistUser(user)
                    isLoading = false
                    onSuccess(user.gender.equals("Male", ignoreCase = true))
                }
                .onFailure { err ->
                    isLoading = false
                    onError(err.message ?: "Sign up failed. Please try again.")
                }
        }
    }

    private fun persistUser(user: UserProfile) {
        userGender = user.gender
        isLoggedIn = true
        prefs.edit()
            .putString("userId", user.userId)
            .putString("display_name", user.name)
            .putString("username", user.username)
            .putString("email", user.email)
            .putString("gender", user.gender)
            .putBoolean("is_logged_in", true)
            .apply()
    }

    fun storedDisplayName(): String =
        prefs.getString("display_name", "Friend") ?: "Friend"

    val storedEmail: String get() = prefs.getString("email", "") ?: ""
    val storedGender: String get() = prefs.getString("gender", "") ?: ""
    val storedUserId: String get() = prefs.getString("userId", "") ?: ""
    val storedUsername: String get() = prefs.getString("username", "") ?: ""

    fun logout() {
        isLoggedIn = false
        prefs.edit().putBoolean("is_logged_in", false).apply()
    }
}
