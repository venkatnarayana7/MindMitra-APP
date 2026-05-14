package com.mindmitra.app.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mindmitra.app.data.community.CommunityComment
import com.mindmitra.app.data.community.CommunityPost
import com.mindmitra.app.data.community.CommunityRepository
import com.mindmitra.app.data.community.CommunityStory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommunityViewModel(application: Application) : AndroidViewModel(application) {

    // ── Feed state ────────────────────────────────────────────────────────────
    val posts = mutableStateListOf<CommunityPost>()
    var isLoading by mutableStateOf(false)
    var isRefreshing by mutableStateOf(false)
    var feedError by mutableStateOf<String?>(null)
    private var cursor: String? = null
    private var hasMore = true

    // ── Stories state ─────────────────────────────────────────────────────────
    val stories = mutableStateListOf<CommunityStory>()
    var storiesLoading by mutableStateOf(false)

    // ── Comment state ─────────────────────────────────────────────────────────
    val comments = mutableStateListOf<CommunityComment>()
    var commentsLoading by mutableStateOf(false)
    var commentSheetPostId by mutableStateOf<String?>(null)

    // ── Upload state ──────────────────────────────────────────────────────────
    var isUploading by mutableStateOf(false)

    init {
        loadFeed()
        loadStories()
        viewModelScope.launch {
            while (true) {
                delay(30_000)
                if (!isLoading && !isRefreshing) {
                    loadFeed(refresh = true)
                    loadStories()
                }
            }
        }
    }

    // ── Feed ──────────────────────────────────────────────────────────────────

    fun loadFeed(refresh: Boolean = false) {
        if (isLoading) return
        if (!hasMore && !refresh) return
        viewModelScope.launch {
            if (refresh) {
                isRefreshing = true
                posts.clear()
                cursor = null
                hasMore = true
            } else {
                isLoading = true
            }
            CommunityRepository.getFeed(if (refresh) null else cursor)
                .onSuccess { page ->
                    posts.addAll(page.posts)
                    cursor = page.cursor
                    hasMore = page.hasMore
                    feedError = null
                }
                .onFailure { feedError = it.message }
            isLoading = false
            isRefreshing = false
        }
    }

    // ── Stories ───────────────────────────────────────────────────────────────

    fun recordStoryView(storyId: String, userId: String) {
        viewModelScope.launch {
            CommunityRepository.recordStoryView(storyId, userId)
                .onSuccess { count ->
                    val i = stories.indexOfFirst { it.storyId == storyId }
                    if (i != -1) stories[i] = stories[i].copy(viewCount = count)
                }
        }
    }

    fun loadStories() {
        viewModelScope.launch {
            storiesLoading = true
            CommunityRepository.getStories()
                .onSuccess { list ->
                    stories.clear()
                    stories.addAll(list)
                }
                .onFailure { /* silent */ }
            storiesLoading = false
        }
    }

    fun createStory(
        userId: String,
        userName: String,
        userAvatar: String,
        imageUri: Uri?,
        text: String,
        onDone: (success: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            isUploading = true
            val imageUrl = if (imageUri != null) uploadImageToS3(imageUri) else null
            CommunityRepository.createStory(userId, userName, userAvatar, imageUrl, text)
                .onSuccess { story ->
                    stories.add(0, story)
                    isUploading = false
                    onDone(true)
                }
                .onFailure {
                    feedError = it.message
                    isUploading = false
                    onDone(false)
                }
        }
    }

    // ── Create post (text-only or with image) ─────────────────────────────────

    fun createPost(
        userId: String,
        userName: String,
        userAvatar: String,
        content: String,
        tags: List<String>,
        imageUri: Uri? = null,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isUploading = imageUri != null
            val imageUrl = if (imageUri != null) uploadImageToS3(imageUri) else null
            isUploading = false
            CommunityRepository.createPost(userId, userName, userAvatar, content, tags, imageUrl)
                .onSuccess { newPost -> posts.add(0, newPost) }
                .onFailure { feedError = it.message }
            onDone()
        }
    }

    private suspend fun uploadImageToS3(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val cr = getApplication<Application>().contentResolver
            val mimeType = cr.getType(uri) ?: "image/jpeg"
            val ext = mimeType.substringAfter("/").let { if (it in listOf("jpeg", "jpg", "png", "webp", "gif")) it else "jpg" }
            val (uploadUrl, publicUrl) = CommunityRepository.getPresignUrl(ext).getOrElse { return@withContext null }
            val bytes = cr.openInputStream(uri)?.readBytes() ?: return@withContext null
            val ok = CommunityRepository.uploadBytesToS3(uploadUrl, bytes, mimeType)
            if (ok) publicUrl else null
        } catch (_: Exception) { null }
    }

    // ── Like / unlike ─────────────────────────────────────────────────────────

    fun toggleLike(postId: String, userId: String) {
        val index = posts.indexOfFirst { it.postId == postId }
        if (index == -1) return
        val post = posts[index]
        val action = if (post.isLikedByMe) "unlike" else "like"
        posts[index] = post.copy(
            isLikedByMe = !post.isLikedByMe,
            likeCount = if (post.isLikedByMe) maxOf(0, post.likeCount - 1) else post.likeCount + 1
        )
        viewModelScope.launch {
            CommunityRepository.toggleLike(postId, userId, action)
                .onSuccess { result ->
                    val i = posts.indexOfFirst { it.postId == postId }
                    if (i != -1) posts[i] = posts[i].copy(likeCount = result.likeCount, isLikedByMe = result.isLiked)
                }
                .onFailure {
                    val i = posts.indexOfFirst { it.postId == postId }
                    if (i != -1) posts[i] = post
                }
        }
    }

    fun toggleSave(postId: String) {
        val i = posts.indexOfFirst { it.postId == postId }
        if (i != -1) posts[i] = posts[i].copy(isSaved = !posts[i].isSaved)
    }

    // ── Comments ──────────────────────────────────────────────────────────────

    fun openComments(postId: String) {
        commentSheetPostId = postId
        comments.clear()
        commentsLoading = true
        viewModelScope.launch {
            CommunityRepository.getComments(postId)
                .onSuccess { list -> comments.addAll(list) }
                .onFailure { feedError = it.message }
            commentsLoading = false
        }
    }

    fun closeComments() { commentSheetPostId = null }

    fun addComment(
        postId: String,
        userId: String,
        userName: String,
        userAvatar: String,
        text: String
    ) {
        if (text.isBlank()) return
        viewModelScope.launch {
            CommunityRepository.addComment(postId, userId, userName, userAvatar, text)
                .onSuccess { comment ->
                    comments.add(comment)
                    val i = posts.indexOfFirst { it.postId == postId }
                    if (i != -1) posts[i] = posts[i].copy(commentCount = posts[i].commentCount + 1)
                }
                .onFailure { feedError = it.message }
        }
    }
}
