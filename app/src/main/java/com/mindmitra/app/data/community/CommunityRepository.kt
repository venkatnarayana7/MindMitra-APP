package com.mindmitra.app.data.community

object CommunityRepository {

    suspend fun getFeed(cursor: String? = null) =
        CommunityApi.getFeed(cursor)

    suspend fun createPost(
        userId: String,
        userName: String,
        userAvatar: String,
        content: String,
        tags: List<String>,
        imageUrl: String? = null
    ) = CommunityApi.createPost(userId, userName, userAvatar, content, tags, imageUrl)

    suspend fun toggleLike(postId: String, userId: String, action: String) =
        CommunityApi.toggleLike(postId, userId, action)

    suspend fun getComments(postId: String) =
        CommunityApi.getComments(postId)

    suspend fun addComment(
        postId: String,
        userId: String,
        userName: String,
        userAvatar: String,
        text: String
    ) = CommunityApi.addComment(postId, userId, userName, userAvatar, text)

    suspend fun getPresignUrl(ext: String = "jpg") =
        CommunityApi.getPresignUrl(ext)
}
