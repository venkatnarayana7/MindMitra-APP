package com.mindmitra.app.data.chat

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_history")
data class ChatHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,       // namespaces history per account
    val role: String,         // "user" | "assistant" | "crisis"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
