package com.mindmitra.app.data.auth

data class UserProfile(
    val userId: String,
    val name: String,
    val username: String = "",
    val email: String,
    val gender: String,
    val createdAt: String = ""
)
