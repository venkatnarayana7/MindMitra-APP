package com.mindmitra.app.data.social

import com.mindmitra.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object FriendApi {

    private val BASE = BuildConfig.COMMUNITY_API_URL
    private val JSON_MT = "application/json; charset=utf-8".toMediaType()
    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun searchUsers(query: String): Result<List<UserSearchResult>> = io {
        val body = get("$BASE/users/search?q=${query.trim()}")
        val arr = body.getJSONArray("users")
        (0 until arr.length()).map {
            val u = arr.getJSONObject(it)
            UserSearchResult(
                userId   = u.optString("userId", ""),
                name     = u.optString("name", ""),
                username = u.optString("username", ""),
                gender   = u.optString("gender", "")
            )
        }
    }

    suspend fun sendRequest(requesterId: String, targetId: String): Result<Unit> = io {
        post("$BASE/friends/request", JSONObject().apply {
            put("requesterId", requesterId)
            put("targetId", targetId)
        })
        Unit
    }

    suspend fun acceptRequest(userId: String, requesterId: String): Result<Unit> = io {
        post("$BASE/friends/accept", JSONObject().apply {
            put("userId", userId)
            put("requesterId", requesterId)
        })
        Unit
    }

    suspend fun removeFriend(userId: String, friendId: String): Result<Unit> = io {
        post("$BASE/friends/remove", JSONObject().apply {
            put("userId", userId)
            put("friendId", friendId)
        })
        Unit
    }

    suspend fun listFriends(userId: String): Result<List<UserSearchResult>> = io {
        val body = get("$BASE/friends/$userId")
        val arr = body.getJSONArray("friends")
        (0 until arr.length()).map {
            val u = arr.getJSONObject(it)
            UserSearchResult(
                userId   = u.optString("userId", ""),
                name     = u.optString("name", ""),
                username = u.optString("username", ""),
                gender   = u.optString("gender", "")
            )
        }
    }

    suspend fun listPendingRequests(userId: String): Result<List<UserSearchResult>> = io {
        val body = get("$BASE/friends/$userId/pending")
        val arr = body.getJSONArray("requests")
        (0 until arr.length()).map {
            val u = arr.getJSONObject(it)
            UserSearchResult(
                userId   = u.optString("userId", ""),
                name     = u.optString("name", ""),
                username = u.optString("username", ""),
                gender   = u.optString("gender", "")
            )
        }
    }

    private fun get(url: String): JSONObject {
        val resp = http.newCall(Request.Builder().url(url).get().build()).execute()
        val raw = resp.body?.string() ?: error("Empty response")
        if (!resp.isSuccessful) error("HTTP ${resp.code}: $raw")
        return JSONObject(raw)
    }

    private fun post(url: String, body: JSONObject): JSONObject {
        val req = Request.Builder().url(url).post(body.toString().toRequestBody(JSON_MT)).build()
        val resp = http.newCall(req).execute()
        val raw = resp.body?.string() ?: error("Empty response")
        if (!resp.isSuccessful) error("HTTP ${resp.code}: $raw")
        return JSONObject(raw)
    }

    private suspend fun <T> io(block: () -> T): Result<T> = withContext(Dispatchers.IO) { runCatching { block() } }
}
