package com.mindmitra.app.data.chat

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_history WHERE userId = :userId ORDER BY timestamp ASC")
    suspend fun getHistory(userId: String): List<ChatHistoryEntity>

    @Insert
    suspend fun insert(msg: ChatHistoryEntity)

    @Query("DELETE FROM chat_history WHERE userId = :userId")
    suspend fun clearHistory(userId: String)
}
