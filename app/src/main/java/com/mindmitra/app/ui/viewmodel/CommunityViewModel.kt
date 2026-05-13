package com.mindmitra.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmitra.app.data.community.CommunityComment
import com.mindmitra.app.data.community.CommunityPost
import com.mindmitra.app.data.community.CommunityRepository
import kotlinx.coroutines.launch

class CommunityViewModel : ViewModel() {

    // ── Feed state ────────────────────────────────────────────────────────────
    val posts = mutableStateListOf<CommunityPost>()
    var isLoading by mutableStateOf(false)
    var isRefreshing by mutableStateOf(false)
    var feedError by mutableStateOf<String?>(null)
    private var cursor: String? = null
    private var hasMore = true

    // ── Comment state ─────────────────────────────────────────────────────────
    val comments = mutableStateListOf<CommunityComment>()
    var commentsLoading by mutableStateOf(false)
    var commentSheetPostId by mutableStateOf<String?>(null)

    init { loadFeed() }

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

    // ── Create post ───────────────────────────────────────────────────────────

    fun createPost(
        userId: String,
        userName: String,
        userAvatar: String,
        content: String,
        tags: List<String>,
        imageUrl: String? = null,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            CommunityRepository.createPost(userId, userName, userAvatar, content, tags, imageUrl)
                .onSuccess { newPost -> posts.add(0, newPost) }
                .onFailure { feedError = it.message }
            onDone()
        }
    }

    // ── Like / unlike ─────────────────────────────────────────────────────────

    fun toggleLike(postId: String, userId: String) {
        val index = posts.indexOfFirst { it.postId == postId }
        if (index == -1) return
        val post = posts[index]
        val action = if (post.isLikedByMe) "unlike" else "like"

        // Optimistic update — feels instant like Instagram
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
                    // Revert on failure
                    val i = posts.indexOfFirst { it.postId == postId }
                    if (i != -1) posts[i] = post
                }
        }
    }

    // ── Save (local only — bookmark) ──────────────────────────────────────────

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
