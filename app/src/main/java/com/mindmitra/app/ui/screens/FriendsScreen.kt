package com.mindmitra.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mindmitra.app.data.social.UserSearchResult
import com.mindmitra.app.ui.theme.*
import com.mindmitra.app.ui.viewmodel.AuthViewModel
import com.mindmitra.app.ui.viewmodel.FriendViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    navController: NavController,
    friendViewModel: FriendViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val myUserId = authViewModel.storedUserId
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(myUserId) { if (myUserId.isNotBlank()) friendViewModel.loadFriends(myUserId) }

    LaunchedEffect(searchQuery) { friendViewModel.searchUsers(searchQuery) }

    friendViewModel.successMsg?.let { msg ->
        LaunchedEffect(msg) { friendViewModel.clearMessages() }
    }

    Scaffold(
        containerColor = DeepNavy,
        topBar = {
            TopAppBar(
                title = { Text("Friends", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepNavy)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(DeepNavy)) {

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by username or name…", color = TextHint, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = AccentLavender) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, null, tint = TextHint)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AccentLavender, unfocusedBorderColor = PrimaryPurple.copy(0.3f),
                    cursorColor = AccentLavender,
                    focusedContainerColor = CardSurface, unfocusedContainerColor = CardSurface
                )
            )

            // Search results (shown when query is active)
            if (searchQuery.length >= 2) {
                if (friendViewModel.searchLoading) {
                    Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                        CircularProgressIndicator(color = AccentLavender, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (friendViewModel.searchResults.isEmpty()) {
                            item {
                                Text("No users found", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(vertical = 16.dp))
                            }
                        }
                        items(friendViewModel.searchResults) { user ->
                            UserRow(
                                user = user,
                                actionLabel = if (friendViewModel.friends.any { it.userId == user.userId }) "Friends" else "Add",
                                actionEnabled = !friendViewModel.friends.any { it.userId == user.userId } && user.userId != myUserId,
                                accentColor = AccentLavender,
                                onAction = { friendViewModel.sendRequest(myUserId, user.userId) }
                            )
                        }
                    }
                }
                return@Column
            }

            // Tabs — My Friends / Requests
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DeepNavy,
                contentColor = AccentLavender,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AccentLavender
                    )
                }
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = {
                    Text("Friends (${friendViewModel.friends.size})", color = if (selectedTab == 0) AccentLavender else TextSecondary)
                })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = {
                    Text("Requests (${friendViewModel.pendingRequests.size})", color = if (selectedTab == 1) AccentLavender else TextSecondary)
                })
            }

            if (friendViewModel.isLoading) {
                Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) {
                    CircularProgressIndicator(color = AccentLavender, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
                }
            } else if (selectedTab == 0) {
                // My Friends
                if (friendViewModel.friends.isEmpty()) {
                    EmptyState(
                        icon = "👥",
                        message = "No friends yet.\nSearch by username to connect!"
                    )
                } else {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(friendViewModel.friends, key = { it.userId }) { user ->
                            UserRow(
                                user = user,
                                actionLabel = "Remove",
                                actionEnabled = true,
                                accentColor = Color(0xFFE57373),
                                onAction = { friendViewModel.removeFriend(myUserId, user.userId) }
                            )
                        }
                    }
                }
            } else {
                // Pending Requests
                if (friendViewModel.pendingRequests.isEmpty()) {
                    EmptyState(icon = "📨", message = "No pending friend requests.")
                } else {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(friendViewModel.pendingRequests, key = { it.userId }) { user ->
                            UserRow(
                                user = user,
                                actionLabel = "Accept",
                                actionEnabled = true,
                                accentColor = Color(0xFF66BB6A),
                                onAction = { friendViewModel.acceptRequest(myUserId, user.userId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserRow(
    user: UserSearchResult,
    actionLabel: String,
    actionEnabled: Boolean,
    accentColor: Color,
    onAction: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).background(
                    if (user.gender.equals("Female", ignoreCase = true)) Color(0xFFFFB6C1).copy(0.3f)
                    else PrimaryPurple.copy(0.3f),
                    CircleShape
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (user.gender.equals("Female", ignoreCase = true)) "💗" else "🧠",
                    fontSize = 20.sp
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text("@${user.username}", fontSize = 12.sp, color = TextSecondary)
            }
            if (actionEnabled) {
                Button(
                    onClick = onAction,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(actionLabel, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            } else {
                Text(actionLabel, fontSize = 12.sp, color = TextHint, modifier = Modifier.padding(horizontal = 8.dp))
            }
        }
    }
}

@Composable
private fun EmptyState(icon: String, message: String) {
    Box(Modifier.fillMaxWidth().padding(48.dp), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 44.sp)
            Spacer(Modifier.height(12.dp))
            Text(message, fontSize = 14.sp, color = TextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 20.sp)
        }
    }
}
