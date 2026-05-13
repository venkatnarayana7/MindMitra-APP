package com.mindmitra.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ApiMessage(val role: String, val content: String)

class GroqService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Groq API key — move to BuildConfig or Secrets Manager before production
    // Replace this key at https://console.groq.com/keys if you see a 401 error
    private val apiKey = BuildConfig.GROQ_API_KEY
    private val endpoint = "https://api.groq.com/openai/v1/chat/completions"
    private val model = "llama-3.1-8b-instant"

    suspend fun chat(messages: List<ApiMessage>): String = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("model", model)
            put("temperature", 0.75)
            put("max_tokens", 200)
            put("messages", JSONArray().apply {
                messages.forEach { msg ->
                    put(JSONObject().apply {
                        put("role", msg.role)
                        put("content", msg.content)
                    })
                }
            })
        }

        val request = Request.Builder()
            .url(endpoint)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(body.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            val rawBody = response.body?.string() ?: throw Exception("Empty response body")
            if (!response.isSuccessful) {
                // Groq error format: { "error": { "message": "...", "type": "..." } }
                val errorMsg = runCatching {
                    JSONObject(rawBody)
                        .getJSONObject("error")
                        .getString("message")
                }.getOrElse { rawBody }
                throw Exception("Groq ${response.code}: $errorMsg")
            }
            JSONObject(rawBody)
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim()
        }
    }
}
