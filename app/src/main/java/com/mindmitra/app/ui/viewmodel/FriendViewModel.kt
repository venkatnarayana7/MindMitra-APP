package com.mindmitra.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmitra.app.data.social.FriendApi
import com.mindmitra.app.data.social.UserSearchResult
import kotlinx.coroutines.launch

class FriendViewModel : ViewModel() {

    val friends = mutableStateListOf<UserSearchResult>()
    val pendingRequests = mutableStateListOf<UserSearchResult>()
    val searchResults = mutableStateListOf<UserSearchResult>()

    var isLoading by mutableStateOf(false)
    var searchLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var successMsg by mutableStateOf<String?>(null)

    fun loadFriends(userId: String) {
        viewModelScope.launch {
            isLoading = true
            FriendApi.listFriends(userId)
                .onSuccess { list -> friends.clear(); friends.addAll(list) }
                .onFailure { error = it.message }
            FriendApi.listPendingRequests(userId)
                .onSuccess { list -> pendingRequests.clear(); pendingRequests.addAll(list) }
                .onFailure { /* silent */ }
            isLoading = false
        }
    }

    fun searchUsers(query: String) {
        if (query.length < 2) { searchResults.clear(); return }
        viewModelScope.launch {
            searchLoading = true
            FriendApi.searchUsers(query)
                .onSuccess { list -> searchResults.clear(); searchResults.addAll(list) }
                .onFailure { error = it.message }
            searchLoading = false
        }
    }

    fun sendRequest(myUserId: String, targetId: String) {
        viewModelScope.launch {
            FriendApi.sendRequest(myUserId, targetId)
                .onSuccess { successMsg = "Friend request sent!" }
                .onFailure { error = it.message }
        }
    }

    fun acceptRequest(myUserId: String, requesterId: String) {
        viewModelScope.launch {
            FriendApi.acceptRequest(myUserId, requesterId)
                .onSuccess {
                    val req = pendingRequests.firstOrNull { it.userId == requesterId }
                    if (req != null) { pendingRequests.remove(req); friends.add(req) }
                    successMsg = "Friend added!"
                }
                .onFailure { error = it.message }
        }
    }

    fun removeFriend(myUserId: String, friendId: String) {
        viewModelScope.launch {
            FriendApi.removeFriend(myUserId, friendId)
                .onSuccess {
                    friends.removeIf { it.userId == friendId }
                    successMsg = "Friend removed."
                }
                .onFailure { error = it.message }
        }
    }

    fun clearMessages() { error = null; successMsg = null }
}
