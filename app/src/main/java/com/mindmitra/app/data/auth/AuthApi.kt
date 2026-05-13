package com.mindmitra.app.data.auth

import com.mindmitra.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object AuthApi {

    private val BASE = BuildConfig.COMMUNITY_API_URL
    private val JSON_MT = "application/json; charset=utf-8".toMediaType()

    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun signup(
        name: String,
        email: String,
        password: String,
        gender: String
    ): Result<UserProfile> = withContext(Dispatchers.IO) {
        runCatching {
            val payload = JSONObject().apply {
                put("name", name.trim())
                put("email", email.trim().lowercase())
                put("password", password)
                put("gender", gender)
            }
            val resp = post("$BASE/auth/signup", payload)
            UserProfile(
                userId    = resp.getString("userId"),
                name      = resp.getString("name"),
                email     = resp.getString("email"),
                gender    = resp.getString("gender"),
                createdAt = resp.optString("createdAt", "")
            )
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<UserProfile> = withContext(Dispatchers.IO) {
        runCatching {
            val payload = JSONObject().apply {
                put("email", email.trim().lowercase())
                put("password", password)
            }
            val resp = post("$BASE/auth/login", payload)
            UserProfile(
                userId    = resp.getString("userId"),
                name      = resp.getString("name"),
                email     = resp.getString("email"),
                gender    = resp.getString("gender"),
                createdAt = resp.optString("createdAt", "")
            )
        }
    }

    private fun post(url: String, body: JSONObject): JSONObject {
        val req = Request.Builder()
            .url(url)
            .post(body.toString().toRequestBody(JSON_MT))
            .build()
        val resp = http.newCall(req).execute()
        val raw = resp.body?.string() ?: error("Empty response")
        if (!resp.isSuccessful) {
            val msg = runCatching { JSONObject(raw).getString("error") }.getOrDefault("HTTP ${resp.code}")
            error(msg)
        }
        return JSONObject(raw)
    }
}
