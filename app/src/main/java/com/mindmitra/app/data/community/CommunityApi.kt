package com.mindmitra.app.data.community

import com.mindmitra.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object CommunityApi {

    private val BASE = BuildConfig.COMMUNITY_API_URL
    private val JSON_MT = "application/json; charset=utf-8".toMediaType()

    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // ── Feed ─────────────────────────────────────────────────────────────────

    suspend fun getFeed(cursor: String? = null): Result<FeedPage> = io {
        val url = buildString {
            append("$BASE/posts")
            if (cursor != null) append("?cursor=$cursor")
        }
        val body = get(url)
        val posts = parsePosts(body.getJSONArray("posts"))
        val nextCursor = if (body.isNull("cursor")) null else body.getString("cursor")
        FeedPage(posts, nextCursor, body.optBoolean("hasMore", false))
    }

    // ── Create post ──────────────────────────────────────────────────────────

    suspend fun createPost(
        userId: String,
        userName: String,
        userAvatar: String,
        content: String,
        tags: List<String>,
        imageUrl: String? = null,
        imageKey: String? = null
    ): Result<CommunityPost> = io {
        val payload = JSONObject().apply {
            put("userId", userId)
            put("userName", userName)
            put("userAvatar", userAvatar)
            put("content", content)
            put("tags", JSONArray(tags))
            if (imageUrl != null) put("imageUrl", imageUrl)
            if (imageKey != null) put("imageKey", imageKey)
            put("hasImage", imageUrl != null || imageKey != null)
        }
        val resp = post("$BASE/posts", payload)
        parsePost(resp.getJSONObject("post"))
    }

    // ── Like / Unlike ────────────────────────────────────────────────────────

    suspend fun toggleLike(postId: String, userId: String, action: String): Result<LikeResult> = io {
        val payload = JSONObject().apply {
            put("userId", userId)
            put("action", action)
        }
        val resp = put("$BASE/posts/$postId/like", payload)
        LikeResult(
            likeCount = resp.optInt("likeCount", 0),
            isLiked = resp.optBoolean("isLiked", action == "like")
        )
    }

    // ── Comments ─────────────────────────────────────────────────────────────

    suspend fun getComments(postId: String): Result<List<CommunityComment>> = io {
        val body = get("$BASE/posts/$postId/comments")
        val arr = body.getJSONArray("comments")
        (0 until arr.length()).map { parseComment(arr.getJSONObject(it)) }
    }

    suspend fun addComment(
        postId: String,
        userId: String,
        userName: String,
        userAvatar: String,
        text: String
    ): Result<CommunityComment> = io {
        val payload = JSONObject().apply {
            put("userId", userId)
            put("userName", userName)
            put("userAvatar", userAvatar)
            put("text", text)
        }
        val resp = post("$BASE/posts/$postId/comments", payload)
        parseComment(resp.getJSONObject("comment"))
    }

    // ── Stories ──────────────────────────────────────────────────────────────

    suspend fun getStories(): Result<List<CommunityStory>> = io {
        val body = get("$BASE/stories")
        val arr = body.getJSONArray("stories")
        (0 until arr.length()).map { parseStory(arr.getJSONObject(it)) }
    }

    suspend fun createStory(
        userId: String,
        userName: String,
        userAvatar: String,
        imageUrl: String?,
        text: String
    ): Result<CommunityStory> = io {
        val payload = JSONObject().apply {
            put("userId", userId)
            put("userName", userName)
            put("userAvatar", userAvatar)
            if (imageUrl != null) put("imageUrl", imageUrl)
            put("text", text)
        }
        val resp = post("$BASE/stories", payload)
        parseStory(resp.getJSONObject("story"))
    }

    // ── Presign upload URL ───────────────────────────────────────────────────

    suspend fun getPresignUrl(ext: String = "jpg"): Result<Pair<String, String>> = io {
        val body = get("$BASE/upload/presign?ext=$ext")
        Pair(body.getString("uploadUrl"), body.getString("publicUrl"))
    }

    // Uploads raw bytes directly to S3 presigned URL (no JSON wrapper)
    suspend fun uploadBytesToS3(uploadUrl: String, bytes: ByteArray, mimeType: String): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                val body = bytes.toRequestBody(mimeType.toMediaType())
                val req = Request.Builder().url(uploadUrl).put(body).build()
                val resp = http.newCall(req).execute()
                resp.isSuccessful
            }.getOrDefault(false)
        }

    // ── HTTP helpers ─────────────────────────────────────────────────────────

    private fun get(url: String): JSONObject {
        val req = Request.Builder().url(url).get().build()
        val resp = http.newCall(req).execute()
        val raw = resp.body?.string() ?: error("Empty response from $url")
        if (!resp.isSuccessful) error("HTTP ${resp.code}: $raw")
        return JSONObject(raw)
    }

    private fun post(url: String, body: JSONObject): JSONObject {
        val req = Request.Builder()
            .url(url)
            .post(body.toString().toRequestBody(JSON_MT))
            .build()
        val resp = http.newCall(req).execute()
        val raw = resp.body?.string() ?: error("Empty response")
        if (!resp.isSuccessful) error("HTTP ${resp.code}: $raw")
        return JSONObject(raw)
    }

    private fun put(url: String, body: JSONObject): JSONObject {
        val req = Request.Builder()
            .url(url)
            .put(body.toString().toRequestBody(JSON_MT))
            .build()
        val resp = http.newCall(req).execute()
        val raw = resp.body?.string() ?: error("Empty response")
        if (!resp.isSuccessful) error("HTTP ${resp.code}: $raw")
        return JSONObject(raw)
    }

    private suspend fun <T> io(block: () -> T): Result<T> = withContext(Dispatchers.IO) {
        runCatching { block() }
    }

    // ── Parsers ──────────────────────────────────────────────────────────────

    private fun parsePosts(arr: JSONArray): List<CommunityPost> =
        (0 until arr.length()).mapNotNull { runCatching { parsePost(arr.getJSONObject(it)) }.getOrNull() }

    private fun parsePost(j: JSONObject): CommunityPost {
        val tagsArr = j.optJSONArray("tags")
        val tags = if (tagsArr != null) (0 until tagsArr.length()).map { tagsArr.optString(it, "") }
                   else emptyList()
        return CommunityPost(
            postId        = j.getString("postId"),
            userId        = j.optString("userId", ""),
            userName      = j.optString("userName", "Anonymous"),
            userAvatar    = j.optString("userAvatar", "🧠"),
            content       = j.optString("content", ""),
            imageUrl      = j.takeIf { !it.isNull("imageUrl") }?.optString("imageUrl"),
            imageKey      = j.takeIf { !it.isNull("imageKey") }?.optString("imageKey"),
            hasImage      = j.optBoolean("hasImage", false),
            tags          = tags,
            likeCount     = j.optInt("likeCount", 0),
            commentCount  = j.optInt("commentCount", 0),
            createdAt     = j.optString("createdAt", ""),
            dominantColor = j.optString("dominantColor", "#1E1B3C")
        )
    }

    private fun parseComment(c: JSONObject) = CommunityComment(
        commentId  = c.getString("commentId"),
        postId     = c.getString("postId"),
        userId     = c.optString("userId", ""),
        userName   = c.optString("userName", "Anonymous"),
        userAvatar = c.optString("userAvatar", "🧠"),
        text       = c.getString("text"),
        createdAt  = c.optString("createdAt", "")
    )

    private fun parseStory(j: JSONObject) = CommunityStory(
        storyId   = j.getString("storyId"),
        userId    = j.optString("userId", ""),
        userName  = j.optString("userName", "Anonymous"),
        userAvatar = j.optString("userAvatar", "🧠"),
        imageUrl  = j.takeIf { !it.isNull("imageUrl") }?.optString("imageUrl"),
        text      = j.optString("text", ""),
        createdAt = j.optString("createdAt", "")
    )
}
