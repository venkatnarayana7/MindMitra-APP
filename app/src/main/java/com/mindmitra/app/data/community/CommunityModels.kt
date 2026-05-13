package com.mindmitra.app.data.community

data class CommunityPost(
    val postId: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val content: String,
    val imageUrl: String?,
    val imageKey: String?,
    val hasImage: Boolean,
    val tags: List<String>,
    val likeCount: Int,
    val commentCount: Int,
    val createdAt: String,
    val dominantColor: String,
    val isLikedByMe: Boolean = false,
    val isSaved: Boolean = false
)

data class CommunityComment(
    val commentId: String,
    val postId: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val text: String,
    val createdAt: String
)

data class FeedPage(
    val posts: List<CommunityPost>,
    val cursor: String?,
    val hasMore: Boolean
)

data class LikeResult(
    val likeCount: Int,
    val isLiked: Boolean
)
