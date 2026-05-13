package com.mindmitra.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mindmitra.app.data.community.CommunityPost
import com.mindmitra.app.ui.theme.GirlBg
import com.mindmitra.app.ui.theme.GirlBorder
import com.mindmitra.app.ui.theme.GirlCard
import com.mindmitra.app.ui.theme.GirlPrimary
import com.mindmitra.app.ui.theme.GirlPrimaryDim
import com.mindmitra.app.ui.theme.GirlSurface
import com.mindmitra.app.ui.theme.GirlTextDark
import com.mindmitra.app.ui.theme.GirlTextLight
import com.mindmitra.app.ui.theme.GirlTextMid
import com.mindmitra.app.ui.viewmodel.AuthViewModel
import com.mindmitra.app.ui.viewmodel.CommunityViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private data class GirlStoryUser(val avatar: String, val name: String, val hasStory: Boolean = true)

private fun String.toGirlRelativeTime(): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val then = sdf.parse(this) ?: return "recently"
        val mins = (Date().time - then.time) / 60_000
        when {
            mins < 1     -> "just now"
            mins < 60    -> "${mins}m ago"
            mins < 1440  -> "${mins / 60}h ago"
            mins < 10080 -> "${mins / 1440}d ago"
            else         -> "${mins / 10080}w ago"
        }
    } catch (_: Exception) { "recently" }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GirlCommunityScreen(
    navController: NavController,
    communityViewModel: CommunityViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val posts = communityViewModel.posts
    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val total = listState.layoutInfo.totalItemsCount
            last != null && total > 0 && last.index >= total - 3
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !communityViewModel.isLoading && !communityViewModel.isRefreshing)
            communityViewModel.loadFeed()
    }

    val currentUserId = authViewModel.storedUserId.ifBlank { authViewModel.storedEmail.ifBlank { "user_local" } }
    val currentUserName = authViewModel.storedDisplayName()
    val currentAvatar = "💗"

    var showPostDialog by remember { mutableStateOf(false) }
    var newPostText by remember { mutableStateOf("") }
    var tagInput by remember { mutableStateOf("") }
    val selectedPostTags = remember { mutableStateListOf<String>() }
    var hasSelectedImage by remember { mutableStateOf(false) }

    val commentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()
    var commentText by remember { mutableStateOf("") }

    val stories = remember {
        listOf(
            GirlStoryUser("💗", "Your\nStory", false),
            GirlStoryUser("🌸", "Ananya"),
            GirlStoryUser("🌺", "Priya"),
            GirlStoryUser("🌷", "Riya"),
            GirlStoryUser("🌻", "Shreya"),
            GirlStoryUser("🌼", "Kavya"),
            GirlStoryUser("🦋", "Nisha"),
        )
    }

    Scaffold(
        containerColor = GirlBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showPostDialog = true },
                containerColor = GirlPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Post", modifier = Modifier.size(22.dp))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(GirlBg)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().statusBarsPadding(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Stories row
                item {
                    LazyRow(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(stories) { story ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(58.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .background(
                                            if (story.hasStory) Brush.sweepGradient(
                                                listOf(GirlPrimary, Color(0xFFFFB3D1), GirlPrimary)
                                            ) else Brush.radialGradient(listOf(GirlPrimaryDim, GirlPrimaryDim)),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(if (story.hasStory) 48.dp else 54.dp)
                                            .background(GirlPrimaryDim, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(story.avatar, fontSize = 22.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    story.name,
                                    fontSize = 10.sp, color = GirlTextMid,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 13.sp,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Pinned banner
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .background(GirlPrimaryDim, RoundedCornerShape(16.dp))
                            .border(1.dp, GirlBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📌", fontSize = 18.sp)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Safe Space Rules", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GirlPrimary)
                                Text("Be kind. Be supportive. You're not alone here. 💗", fontSize = 12.sp, color = GirlTextMid)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Live feed from AWS DynamoDB
                items(posts, key = { it.postId }) { post ->
                    GirlLivePostCard(
                        post = post,
                        onLike = { communityViewModel.toggleLike(post.postId, currentUserId) },
                        onSave = { communityViewModel.toggleSave(post.postId) },
                        onComment = {
                            communityViewModel.openComments(post.postId)
                            scope.launch { commentSheetState.show() }
                        }
                    )
                }

                // Loading indicator
                if (communityViewModel.isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                            CircularProgressIndicator(
                                color = GirlPrimary, strokeWidth = 2.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Empty state
                if (!communityViewModel.isLoading && posts.isEmpty() && communityViewModel.feedError == null) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🌸", fontSize = 40.sp)
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "Be the first to share something 💗",
                                    fontSize = 14.sp, color = GirlTextMid, textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Error / retry
                communityViewModel.feedError?.let { err ->
                    item {
                        Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Could not load feed", fontSize = 13.sp, color = GirlTextMid)
                                Spacer(Modifier.height(8.dp))
                                TextButton(onClick = { communityViewModel.loadFeed(refresh = true) }) {
                                    Text("Retry", color = GirlPrimary)
                                }
                            }
                        }
                    }
                }
            }

            // Refresh indicator
            AnimatedVisibility(
                visible = communityViewModel.isRefreshing,
                enter = fadeIn(), exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding()
            ) {
                Box(Modifier.padding(top = 60.dp), Alignment.Center) {
                    CircularProgressIndicator(
                        color = GirlPrimary, strokeWidth = 2.dp, modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    // ── Comment Bottom Sheet ──────────────────────────────────────────────────
    if (communityViewModel.commentSheetPostId != null) {
        ModalBottomSheet(
            onDismissRequest = { communityViewModel.closeComments(); commentText = "" },
            sheetState = commentSheetState,
            containerColor = GirlCard,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .background(GirlBorder, RoundedCornerShape(2.dp))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    "Comments 💬",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GirlTextDark,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                if (communityViewModel.commentsLoading) {
                    Box(Modifier.fillMaxWidth().height(80.dp), Alignment.Center) {
                        CircularProgressIndicator(
                            color = GirlPrimary, strokeWidth = 2.dp, modifier = Modifier.size(24.dp)
                        )
                    }
                } else if (communityViewModel.comments.isEmpty()) {
                    Text(
                        "No comments yet. Be the first! 💗",
                        fontSize = 13.sp, color = GirlTextMid,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.height(300.dp)) {
                        items(communityViewModel.comments, key = { it.commentId }) { c ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(GirlPrimaryDim, CircleShape)
                                        .border(1.dp, GirlBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) { Text(c.userAvatar, fontSize = 14.sp) }
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        c.userName,
                                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = GirlTextDark
                                    )
                                    Text(c.text, fontSize = 13.sp, color = GirlTextMid, lineHeight = 18.sp)
                                    Text(c.createdAt.toGirlRelativeTime(), fontSize = 11.sp, color = GirlTextLight)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Add a comment…", color = GirlTextLight, fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GirlTextDark,
                            unfocusedTextColor = GirlTextDark,
                            focusedBorderColor = GirlPrimary,
                            unfocusedBorderColor = GirlBorder,
                            cursorColor = GirlPrimary,
                            focusedContainerColor = GirlSurface,
                            unfocusedContainerColor = GirlSurface
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    val postId = communityViewModel.commentSheetPostId ?: ""
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                communityViewModel.addComment(
                                    postId, currentUserId, currentUserName, currentAvatar, commentText
                                )
                                commentText = ""
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Send, null,
                            tint = if (commentText.isNotBlank()) GirlPrimary else GirlTextLight,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }

    // ── New Post Dialog ───────────────────────────────────────────────────────
    if (showPostDialog) {
        val popularTags = listOf("motivation", "wellness", "healing", "selfcare", "anxiety", "gratitude", "mentalhealth", "support")
        fun dismissDialog() {
            showPostDialog = false; newPostText = ""; tagInput = ""
            selectedPostTags.clear(); hasSelectedImage = false
        }
        Dialog(onDismissRequest = { dismissDialog() }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GirlCard),
                border = BorderStroke(1.dp, GirlBorder)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("New Post 💕", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = GirlTextDark)
                        IconButton(onClick = { dismissDialog() }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, "Close", tint = GirlTextMid, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newPostText,
                        onValueChange = { newPostText = it },
                        placeholder = { Text("What's on your mind?", color = GirlTextLight, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GirlTextDark, unfocusedTextColor = GirlTextDark,
                            focusedBorderColor = GirlPrimary, unfocusedBorderColor = GirlBorder,
                            cursorColor = GirlPrimary, focusedContainerColor = GirlSurface,
                            unfocusedContainerColor = GirlSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (!hasSelectedImage) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GirlSurface, RoundedCornerShape(12.dp))
                                .border(1.dp, GirlBorder, RoundedCornerShape(12.dp))
                                .clickable { hasSelectedImage = true }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Image, null, tint = GirlPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Photo", fontSize = 13.sp, color = GirlPrimary, fontWeight = FontWeight.Medium)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .background(
                                    Brush.verticalGradient(listOf(Color(0xFFFFE4EF), Color(0xFFFFF0F5))),
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRect(Brush.verticalGradient(listOf(Color(0xFFFCE4EC), Color(0xFFFFF0F5))))
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        listOf(Color(0x60EF5DA8), Color(0x20EF5DA8), Color.Transparent),
                                        center = Offset(size.width * 0.5f, size.height * 0.35f),
                                        radius = size.width * 0.35f
                                    ),
                                    radius = size.width * 0.35f,
                                    center = Offset(size.width * 0.5f, size.height * 0.35f)
                                )
                                val hillPath = Path().apply {
                                    val w = size.width; val h = size.height
                                    moveTo(0f, h); lineTo(0f, h * 0.75f)
                                    lineTo(w * 0.25f, h * 0.55f); lineTo(w * 0.5f, h * 0.7f)
                                    lineTo(w * 0.72f, h * 0.5f); lineTo(w, h * 0.68f); lineTo(w, h); close()
                                }
                                drawPath(hillPath, Color(0x30EF5DA8))
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd).padding(8.dp)
                                    .background(Color(0x66EF5DA8), RoundedCornerShape(8.dp))
                                    .clickable { hasSelectedImage = false }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("✕ Remove", fontSize = 11.sp, color = Color.White)
                            }
                            Text("🌸", fontSize = 32.sp, modifier = Modifier.align(Alignment.Center))
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Add Hashtags", fontSize = 12.sp, color = GirlTextMid, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = tagInput,
                            onValueChange = { input ->
                                val clean = input.trimStart('#')
                                if (clean.endsWith(" ") || clean.endsWith(",")) {
                                    val tag = clean.trimEnd(' ', ',').trim()
                                    if (tag.isNotBlank() && !selectedPostTags.contains(tag)) selectedPostTags.add(tag)
                                    tagInput = ""
                                } else tagInput = clean
                            },
                            placeholder = { Text("wellness, healing…", fontSize = 12.sp, color = GirlTextLight) },
                            prefix = { Text("#", color = GirlPrimary, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = GirlTextDark, unfocusedTextColor = GirlTextDark,
                                focusedBorderColor = GirlPrimary, unfocusedBorderColor = GirlBorder,
                                cursorColor = GirlPrimary, focusedContainerColor = GirlSurface,
                                unfocusedContainerColor = GirlSurface
                            )
                        )
                        Button(
                            onClick = {
                                val tag = tagInput.trim().trimStart('#')
                                if (tag.isNotBlank() && !selectedPostTags.contains(tag)) selectedPostTags.add(tag)
                                tagInput = ""
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GirlPrimary),
                            modifier = Modifier.height(52.dp)
                        ) { Text("Add", fontSize = 13.sp, color = Color.White) }
                    }

                    if (selectedPostTags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(selectedPostTags.toList()) { tag ->
                                Row(
                                    modifier = Modifier
                                        .background(GirlPrimaryDim, RoundedCornerShape(20.dp))
                                        .border(1.dp, GirlBorder, RoundedCornerShape(20.dp))
                                        .padding(start = 10.dp, end = 6.dp, top = 5.dp, bottom = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("#$tag", fontSize = 12.sp, color = GirlPrimary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.Close, null, tint = GirlPrimary.copy(0.6f),
                                        modifier = Modifier.size(12.dp).clickable { selectedPostTags.remove(tag) }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Popular tags", fontSize = 11.sp, color = GirlTextLight)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(popularTags) { tag ->
                            val isAdded = selectedPostTags.contains(tag)
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isAdded) GirlPrimaryDim else GirlSurface,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isAdded) GirlBorder else GirlBorder.copy(alpha = 0.5f),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { if (!isAdded) selectedPostTags.add(tag) else selectedPostTags.remove(tag) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    "#$tag", fontSize = 11.sp,
                                    color = if (isAdded) GirlPrimary else GirlTextMid
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(onClick = { dismissDialog() }, modifier = Modifier.weight(1f)) {
                            Text("Cancel", color = GirlTextMid)
                        }
                        Button(
                            onClick = {
                                val text = newPostText.trim()
                                if (text.isNotEmpty()) {
                                    communityViewModel.createPost(
                                        userId = currentUserId,
                                        userName = currentUserName,
                                        userAvatar = currentAvatar,
                                        content = text,
                                        tags = selectedPostTags.toList(),
                                        onDone = { dismissDialog() }
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GirlPrimary)
                        ) {
                            Text("Post 💗", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

// ── Girl Post Card — pink theme, wired to live AWS data ──────────────────────

@Composable
private fun GirlLivePostCard(
    post: CommunityPost,
    onLike: () -> Unit,
    onSave: () -> Unit,
    onComment: () -> Unit
) {
    var likeScale by remember { mutableStateOf(1f) }
    val animScale by animateFloatAsState(
        targetValue = likeScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        finishedListener = { likeScale = 1f },
        label = "girlLikeScale"
    )

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GirlCard),
        border = BorderStroke(1.dp, GirlBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Author row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(GirlPrimaryDim, CircleShape)
                        .border(2.dp, GirlBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val av = post.userAvatar
                    if (av.isNotEmpty() && av.any { it.code > 127 }) {
                        Text(av, fontSize = 20.sp)
                    } else {
                        Icon(Icons.Default.Person, null, tint = GirlPrimary, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.userName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = GirlTextDark)
                    Text(post.createdAt.toGirlRelativeTime(), fontSize = 11.sp, color = GirlTextLight)
                }
                if (post.tags.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .background(GirlPrimaryDim, RoundedCornerShape(20.dp))
                            .border(1.dp, GirlBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(post.tags.first(), fontSize = 11.sp, color = GirlPrimary, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(post.content, fontSize = 14.sp, color = GirlTextDark, lineHeight = 21.sp)

            // Image — real S3 URL via Coil, or pink canvas placeholder
            if (post.hasImage) {
                Spacer(modifier = Modifier.height(10.dp))
                if (!post.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = post.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                runCatching {
                                    Color(android.graphics.Color.parseColor(
                                        post.dominantColor.takeIf { it.startsWith("#") } ?: "#FCE4EC"
                                    ))
                                }.getOrDefault(Color(0xFFFCE4EC)),
                                RoundedCornerShape(12.dp)
                            )
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(
                                Brush.verticalGradient(listOf(Color(0xFFFCE4EC), Color(0xFFFFF0F5))),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    listOf(Color(0x60EF5DA8), Color(0x20EF5DA8), Color.Transparent),
                                    center = Offset(size.width * 0.5f, size.height * 0.4f),
                                    radius = size.width * 0.3f
                                ),
                                radius = size.width * 0.3f,
                                center = Offset(size.width * 0.5f, size.height * 0.4f)
                            )
                            val hillPath = Path().apply {
                                val w = size.width; val h = size.height
                                moveTo(0f, h); lineTo(0f, h * 0.75f)
                                lineTo(w * 0.25f, h * 0.55f); lineTo(w * 0.5f, h * 0.7f)
                                lineTo(w * 0.72f, h * 0.5f); lineTo(w, h * 0.68f); lineTo(w, h); close()
                            }
                            drawPath(hillPath, Color(0x30EF5DA8))
                        }
                        Text("🌸", fontSize = 36.sp)
                    }
                }
            }

            // Extra hashtag chips (tags beyond the first shown in header)
            if (post.tags.size > 1) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(post.tags.drop(1).take(3)) { tag ->
                        Box(
                            modifier = Modifier
                                .background(GirlPrimaryDim, RoundedCornerShape(20.dp))
                                .border(1.dp, GirlBorder, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("#$tag", fontSize = 11.sp, color = GirlPrimary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reaction row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like with spring animation
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        likeScale = 1.4f
                        onLike()
                    }
                ) {
                    Icon(
                        imageVector = if (post.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLikedByMe) GirlPrimary else GirlTextLight,
                        modifier = Modifier.size(18.dp).scale(animScale)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(post.likeCount.toString(), fontSize = 13.sp, color = if (post.isLikedByMe) GirlPrimary else GirlTextMid)
                }

                Spacer(modifier = Modifier.width(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onComment() }
                ) {
                    Icon(Icons.Default.ChatBubbleOutline, "Comment", tint = GirlTextLight, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(post.commentCount.toString(), fontSize = 13.sp, color = GirlTextMid)
                }

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    Icons.Default.Send, "Share", tint = GirlTextLight,
                    modifier = Modifier.size(18.dp).clickable { }
                )

                Spacer(modifier = Modifier.width(18.dp))

                Icon(
                    imageVector = if (post.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Save",
                    tint = if (post.isSaved) GirlPrimary else GirlTextLight,
                    modifier = Modifier.size(20.dp).clickable { onSave() }
                )
            }
        }
    }
}
