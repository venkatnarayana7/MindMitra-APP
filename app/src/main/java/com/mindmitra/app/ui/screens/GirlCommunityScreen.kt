package com.mindmitra.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RemoveRedEye
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.mindmitra.app.navigation.Routes
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.mindmitra.app.data.community.CommunityPost
import com.mindmitra.app.data.community.CommunityStory
import com.mindmitra.app.ui.theme.GirlBg
import com.mindmitra.app.ui.theme.GirlBorder
import com.mindmitra.app.ui.theme.GirlCard
import com.mindmitra.app.ui.theme.GirlPrimary
import com.mindmitra.app.ui.theme.GirlPrimaryDim
import com.mindmitra.app.ui.theme.GirlSurface
import com.mindmitra.app.ui.theme.GirlTextDark
import com.mindmitra.app.ui.theme.GirlTextMid
import com.mindmitra.app.ui.viewmodel.AuthViewModel
import com.mindmitra.app.ui.viewmodel.CommunityViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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

    // ── Post dialog state ─────────────────────────────────────────────────────
    var showPostDialog by remember { mutableStateOf(false) }
    var newPostText by remember { mutableStateOf("") }
    var tagInput by remember { mutableStateOf("") }
    val selectedPostTags = remember { mutableStateListOf<String>() }
    var selectedPostImageUri by remember { mutableStateOf<Uri?>(null) }
    var isPostPublic by remember { mutableStateOf(true) }

    val postImagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) selectedPostImageUri = uri }

    // ── Story state ───────────────────────────────────────────────────────────
    var showStoryDialog by remember { mutableStateOf(false) }
    var storyText by remember { mutableStateOf("") }
    var selectedStoryImageUri by remember { mutableStateOf<Uri?>(null) }
    var viewingStory by remember { mutableStateOf<CommunityStory?>(null) }

    val storyImagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) selectedStoryImageUri = uri }

    // Progress Animatable — lives at composable scope, not inside Dialog
    val storyProgress = remember { Animatable(0f) }
    LaunchedEffect(viewingStory?.storyId) {
        val story = viewingStory
        if (story != null) {
            storyProgress.snapTo(0f)
            communityViewModel.recordStoryView(story.storyId, currentUserId)
            val duration = if (!story.imageUrl.isNullOrBlank()) 7000 else 5000
            storyProgress.animateTo(1f, animationSpec = tween(duration, easing = LinearEasing))
            viewingStory = null
        }
    }

    // ── Comment sheet ─────────────────────────────────────────────────────────
    val commentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()
    var commentText by remember { mutableStateOf("") }

    fun dismissPostDialog() {
        showPostDialog = false; newPostText = ""; tagInput = ""
        selectedPostTags.clear(); selectedPostImageUri = null; isPostPublic = true
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
                // ── Header: title + friends button ────────────────────────────
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Community 💕", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GirlTextDark)
                        TextButton(onClick = { navController.navigate(Routes.GIRL_FRIENDS) }) {
                            Icon(Icons.Default.Group, null, tint = GirlPrimary, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Friends", color = GirlPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // ── Stories row ────────────────────────────────────────────────
                item {
                    LazyRow(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        // "Your Story" add button
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(64.dp)
                                    .clickable { showStoryDialog = true }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .background(GirlPrimaryDim, CircleShape)
                                        .border(2.dp, GirlPrimary.copy(0.5f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("💗", fontSize = 22.sp)
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .size(18.dp)
                                            .background(GirlPrimary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Your\nStory", fontSize = 10.sp, color = GirlTextMid,
                                    textAlign = TextAlign.Center, lineHeight = 13.sp, maxLines = 2
                                )
                            }
                        }
                        // Live stories — show view count below name
                        items(communityViewModel.stories) { story ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(64.dp)
                                    .clickable { viewingStory = story }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .background(
                                            Brush.sweepGradient(listOf(GirlPrimary, Color(0xFFFFB3D1), GirlPrimary)),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(GirlPrimaryDim, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!story.imageUrl.isNullOrBlank()) {
                                            AsyncImage(
                                                model = story.imageUrl,
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                                            )
                                        } else {
                                            Text(story.userAvatar, fontSize = 22.sp)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    story.userName.take(8), fontSize = 10.sp, color = GirlTextMid,
                                    textAlign = TextAlign.Center, lineHeight = 13.sp, maxLines = 1
                                )
                                if (story.viewCount > 0) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            Icons.Default.RemoveRedEye, null,
                                            tint = GirlPrimary.copy(0.7f),
                                            modifier = Modifier.size(9.dp)
                                        )
                                        Spacer(Modifier.width(2.dp))
                                        Text(
                                            "${story.viewCount}", fontSize = 9.sp,
                                            color = GirlPrimary.copy(0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Safe space banner
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

                // ── Live feed ──────────────────────────────────────────────────
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

                if (communityViewModel.isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                            CircularProgressIndicator(color = GirlPrimary, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                        }
                    }
                }

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

                communityViewModel.feedError?.let { _ ->
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

            AnimatedVisibility(
                visible = communityViewModel.isRefreshing,
                enter = fadeIn(), exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding()
            ) {
                Box(Modifier.padding(top = 60.dp), Alignment.Center) {
                    CircularProgressIndicator(color = GirlPrimary, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                }
            }
        }
    }

    // ── Full-screen Story Viewer (Instagram/WhatsApp style — pink theme) ───────
    if (viewingStory != null) {
        val story = viewingStory!!
        val storyImageUrl = story.imageUrl  // snapshot URL before entering Dialog scope
        val ctx = LocalContext.current
        Dialog(
            onDismissRequest = { viewingStory = null },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { viewingStory = null }
            ) {
                // Full-screen image — use SubcomposeAsyncImage with explicit context so
                // Coil correctly reloads from cache on every re-open of the dialog window
                if (!storyImageUrl.isNullOrBlank()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(ctx)
                            .data(storyImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    color = Color.White.copy(0.7f),
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    )
                } else {
                    // Text-only: pink gradient background
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFFFB3D1), Color(0xFFEF5DA8), Color(0xFFFFB3D1))
                                )
                            )
                    )
                }

                // Top gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(listOf(Color.Black.copy(0.70f), Color.Transparent))
                        )
                )

                // Bottom gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.80f)))
                        )
                )

                // ── Top bar: progress + author + close ────────────────────────
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Thin progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(0.35f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(storyProgress.value)
                                .background(Color.White, RoundedCornerShape(2.dp))
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color.White.copy(0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(story.userAvatar, fontSize = 18.sp)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                story.userName,
                                fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White
                            )
                            Text(
                                story.createdAt.toGirlRelativeTime(),
                                fontSize = 11.sp, color = Color.White.copy(0.7f)
                            )
                        }
                        IconButton(
                            onClick = { viewingStory = null },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Close, null,
                                tint = Color.White, modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // ── Bottom: caption + view count ──────────────────────────────
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (story.text.isNotBlank()) {
                        Text(
                            story.text,
                            fontSize = 17.sp, color = Color.White, lineHeight = 25.sp,
                            fontWeight = FontWeight.Medium, textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color.White.copy(0.15f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Default.RemoveRedEye, null,
                            tint = Color.White, modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "${story.viewCount} ${if (story.viewCount == 1) "view" else "views"}",
                            fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    // ── Story Creation Dialog ─────────────────────────────────────────────────
    if (showStoryDialog) {
        Dialog(onDismissRequest = { showStoryDialog = false; storyText = ""; selectedStoryImageUri = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GirlCard),
                border = BorderStroke(1.dp, GirlBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Add to Story 🌸", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = GirlTextDark)
                        IconButton(
                            onClick = { showStoryDialog = false; storyText = ""; selectedStoryImageUri = null },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, null, tint = GirlTextMid, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(Modifier.height(12.dp))

                    if (selectedStoryImageUri == null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GirlPrimaryDim, RoundedCornerShape(12.dp))
                                .border(1.dp, GirlBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    storyImagePicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .padding(horizontal = 14.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, null, tint = GirlPrimary, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Add Photo (optional)", fontSize = 13.sp, color = GirlPrimary, fontWeight = FontWeight.Medium)
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                            AsyncImage(
                                model = selectedStoryImageUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd).padding(8.dp)
                                    .background(Color(0x99000000), RoundedCornerShape(8.dp))
                                    .clickable { selectedStoryImageUri = null }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) { Text("✕", fontSize = 13.sp, color = Color.White) }
                        }
                    }
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = storyText,
                        onValueChange = { storyText = it },
                        placeholder = { Text("What's on your mind?", color = GirlTextMid, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GirlTextDark, unfocusedTextColor = GirlTextDark,
                            focusedBorderColor = GirlPrimary, unfocusedBorderColor = GirlBorder,
                            cursorColor = GirlPrimary,
                            focusedContainerColor = GirlSurface, unfocusedContainerColor = GirlSurface
                        )
                    )
                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (storyText.isNotBlank() || selectedStoryImageUri != null) {
                                communityViewModel.createStory(
                                    userId = currentUserId,
                                    userName = currentUserName,
                                    userAvatar = currentAvatar,
                                    imageUri = selectedStoryImageUri,
                                    text = storyText.trim(),
                                    onDone = { showStoryDialog = false; storyText = ""; selectedStoryImageUri = null }
                                )
                            }
                        },
                        enabled = !communityViewModel.isUploading && (storyText.isNotBlank() || selectedStoryImageUri != null),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GirlPrimary)
                    ) {
                        if (communityViewModel.isUploading) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                        } else {
                            Text("Share Story 💗", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
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
                    "Comments 💬", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GirlTextDark,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                if (communityViewModel.commentsLoading) {
                    Box(Modifier.fillMaxWidth().height(80.dp), Alignment.Center) {
                        CircularProgressIndicator(color = GirlPrimary, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                    }
                } else if (communityViewModel.comments.isEmpty()) {
                    Text(
                        "No comments yet. Be the first! 💗",
                        fontSize = 13.sp, color = GirlTextMid, modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.height(300.dp)) {
                        items(communityViewModel.comments, key = { it.commentId }) { c ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                Box(
                                    modifier = Modifier.size(32.dp).background(GirlPrimaryDim, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) { Text(c.userAvatar, fontSize = 14.sp) }
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(c.userName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = GirlTextDark)
                                    Text(c.text, fontSize = 13.sp, color = GirlTextMid, lineHeight = 18.sp)
                                    Text(c.createdAt.toGirlRelativeTime(), fontSize = 11.sp, color = GirlBorder)
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
                        placeholder = { Text("Add a comment…", color = GirlTextMid, fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GirlTextDark, unfocusedTextColor = GirlTextDark,
                            focusedBorderColor = GirlPrimary, unfocusedBorderColor = GirlBorder,
                            cursorColor = GirlPrimary,
                            focusedContainerColor = GirlSurface, unfocusedContainerColor = GirlSurface
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    val postId = communityViewModel.commentSheetPostId ?: ""
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                communityViewModel.addComment(postId, currentUserId, currentUserName, currentAvatar, commentText)
                                commentText = ""
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Send, null,
                            tint = if (commentText.isNotBlank()) GirlPrimary else GirlTextMid,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }

    // ── New Post Dialog ───────────────────────────────────────────────────────
    if (showPostDialog) {
        val popularTags = listOf("selfcare", "anxiety", "wellness", "healing", "mindfulness", "gratitude", "mentalhealth", "girlpower")
        Dialog(onDismissRequest = { dismissPostDialog() }) {
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
                        Text("New Post 🌸", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = GirlTextDark)
                        IconButton(onClick = { dismissPostDialog() }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, "Close", tint = GirlTextMid, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newPostText,
                        onValueChange = { newPostText = it },
                        placeholder = { Text("What's on your mind?", color = GirlTextMid, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GirlTextDark, unfocusedTextColor = GirlTextDark,
                            focusedBorderColor = GirlPrimary, unfocusedBorderColor = GirlBorder,
                            cursorColor = GirlPrimary,
                            focusedContainerColor = GirlSurface, unfocusedContainerColor = GirlSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (selectedPostImageUri == null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GirlPrimaryDim, RoundedCornerShape(12.dp))
                                .border(1.dp, GirlBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    postImagePicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Image, null, tint = GirlPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Photo", fontSize = 13.sp, color = GirlPrimary, fontWeight = FontWeight.Medium)
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                            AsyncImage(
                                model = selectedPostImageUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd).padding(8.dp)
                                    .background(Color(0x99000000), RoundedCornerShape(8.dp))
                                    .clickable { selectedPostImageUri = null }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) { Text("✕ Remove", fontSize = 11.sp, color = Color.White) }
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
                            placeholder = { Text("wellness, healing…", fontSize = 12.sp, color = GirlTextMid) },
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
                                        Icons.Default.Close, null, tint = GirlPrimary.copy(0.7f),
                                        modifier = Modifier.size(12.dp).clickable { selectedPostTags.remove(tag) }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Popular tags", fontSize = 11.sp, color = GirlTextMid)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(popularTags) { tag ->
                            val isAdded = selectedPostTags.contains(tag)
                            Box(
                                modifier = Modifier
                                    .background(if (isAdded) GirlPrimaryDim else GirlSurface, RoundedCornerShape(20.dp))
                                    .border(1.dp, if (isAdded) GirlPrimary.copy(0.6f) else GirlBorder, RoundedCornerShape(20.dp))
                                    .clickable { if (!isAdded) selectedPostTags.add(tag) else selectedPostTags.remove(tag) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text("#$tag", fontSize = 11.sp, color = if (isAdded) GirlPrimary else GirlTextMid)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Privacy toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GirlSurface, RoundedCornerShape(12.dp))
                            .border(1.dp, if (isPostPublic) GirlPrimary.copy(0.4f) else GirlBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (isPostPublic) Icons.Default.LockOpen else Icons.Default.Lock,
                                null,
                                tint = if (isPostPublic) GirlPrimary else GirlTextMid,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    if (isPostPublic) "Public" else "Friends only",
                                    fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                                    color = if (isPostPublic) GirlPrimary else GirlTextDark
                                )
                                Text(
                                    if (isPostPublic) "Everyone can see this post" else "Only your friends will see this",
                                    fontSize = 11.sp, color = GirlTextMid.copy(0.7f)
                                )
                            }
                        }
                        Switch(
                            checked = isPostPublic,
                            onCheckedChange = { isPostPublic = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GirlPrimary,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = GirlTextMid.copy(0.4f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(onClick = { dismissPostDialog() }, modifier = Modifier.weight(1f)) {
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
                                        imageUri = selectedPostImageUri,
                                        isPublic = isPostPublic,
                                        onDone = { dismissPostDialog() }
                                    )
                                }
                            },
                            enabled = !communityViewModel.isUploading && newPostText.isNotBlank(),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GirlPrimary)
                        ) {
                            if (communityViewModel.isUploading) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                            } else {
                                Text("Post 💗", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Post Card ─────────────────────────────────────────────────────────────────

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
        label = "likeScale"
    )

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GirlCard),
        border = BorderStroke(1.dp, GirlBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).background(GirlPrimaryDim, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val av = post.userAvatar
                    if (av.isNotEmpty() && av.any { it.code > 127 }) {
                        Text(av, fontSize = 18.sp)
                    } else {
                        Icon(Icons.Default.Person, null, tint = GirlPrimary, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.userName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = GirlTextDark)
                    Text(post.createdAt.toGirlRelativeTime(), fontSize = 11.sp, color = GirlTextMid)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(post.content, fontSize = 14.sp, color = GirlTextDark, lineHeight = 21.sp)

            if (post.hasImage && !post.imageUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GirlPrimaryDim, RoundedCornerShape(12.dp))
                )
            }

            if (post.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    post.tags.take(3).forEach { tag ->
                        Box(
                            modifier = Modifier
                                .background(GirlPrimaryDim, RoundedCornerShape(50.dp))
                                .border(1.dp, GirlBorder, RoundedCornerShape(50.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) { Text(tag, color = GirlPrimary, fontSize = 11.sp) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { likeScale = 1.4f; onLike() }
                ) {
                    Icon(
                        imageVector = if (post.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLikedByMe) GirlPrimary else GirlTextMid,
                        modifier = Modifier.size(18.dp).scale(animScale)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(post.likeCount.toString(), fontSize = 13.sp, color = GirlTextMid)
                }
                Spacer(modifier = Modifier.width(22.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onComment() }) {
                    Icon(Icons.Default.ChatBubbleOutline, "Comment", tint = GirlTextMid, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(post.commentCount.toString(), fontSize = 13.sp, color = GirlTextMid)
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.Send, "Share", tint = GirlTextMid, modifier = Modifier.size(18.dp).clickable { })
                Spacer(modifier = Modifier.width(18.dp))
                Icon(
                    imageVector = if (post.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Save",
                    tint = if (post.isSaved) GirlPrimary else GirlTextMid,
                    modifier = Modifier.size(20.dp).clickable { onSave() }
                )
            }
        }
    }
}
