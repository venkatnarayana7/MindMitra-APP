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
import androidx.compose.material.icons.filled.Image
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
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.mindmitra.app.data.community.CommunityPost
import com.mindmitra.app.data.community.CommunityStory
import com.mindmitra.app.ui.theme.AccentLavender
import com.mindmitra.app.ui.theme.CardSurface
import com.mindmitra.app.ui.theme.DeepNavy
import com.mindmitra.app.ui.theme.PrimaryPurple
import com.mindmitra.app.ui.theme.TextHint
import com.mindmitra.app.ui.theme.TextPrimary
import com.mindmitra.app.ui.theme.TextSecondary
import com.mindmitra.app.ui.viewmodel.AuthViewModel
import com.mindmitra.app.ui.viewmodel.CommunityViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private fun String.toRelativeTime(): String {
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
fun CommunityScreen(
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
    val currentAvatar = if (authViewModel.isMale) "🧠" else "💗"

    // ── Post dialog state ─────────────────────────────────────────────────────
    var showPostDialog by remember { mutableStateOf(false) }
    var newPostText by remember { mutableStateOf("") }
    var tagInput by remember { mutableStateOf("") }
    val selectedPostTags = remember { mutableStateListOf<String>() }
    var selectedPostImageUri by remember { mutableStateOf<Uri?>(null) }

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

    // Progress bar Animatable for story viewer — lives outside the dialog so it persists
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
        selectedPostTags.clear(); selectedPostImageUri = null
    }

    Scaffold(
        containerColor = DeepNavy,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showPostDialog = true },
                containerColor = PrimaryPurple,
                contentColor = TextPrimary,
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
                .background(DeepNavy)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().statusBarsPadding(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── Stories row ────────────────────────────────────────────────
                item {
                    LazyRow(
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
                                        .background(
                                            Brush.radialGradient(listOf(Color(0xFF2A2850), Color(0xFF1E1B4A))),
                                            CircleShape
                                        )
                                        .border(2.dp, PrimaryPurple.copy(0.5f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(currentAvatar, fontSize = 22.sp)
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .size(18.dp)
                                            .background(PrimaryPurple, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Your\nStory", fontSize = 10.sp, color = TextSecondary,
                                    textAlign = TextAlign.Center, lineHeight = 13.sp, maxLines = 2
                                )
                            }
                        }
                        // Live stories from AWS — show view count below name
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
                                            Brush.sweepGradient(
                                                listOf(Color(0xFF6C5CE7), Color(0xFF9B8BFA), Color(0xFF6C5CE7))
                                            ),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(Color(0xFF1E1B4A), CircleShape),
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
                                    story.userName.take(8), fontSize = 10.sp, color = TextSecondary,
                                    textAlign = TextAlign.Center, lineHeight = 13.sp, maxLines = 1
                                )
                                if (story.viewCount > 0) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            Icons.Default.RemoveRedEye, null,
                                            tint = AccentLavender.copy(0.7f),
                                            modifier = Modifier.size(9.dp)
                                        )
                                        Spacer(Modifier.width(2.dp))
                                        Text(
                                            "${story.viewCount}", fontSize = 9.sp,
                                            color = AccentLavender.copy(0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // ── Live feed ──────────────────────────────────────────────────
                items(posts, key = { it.postId }) { post ->
                    LivePostCard(
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
                            CircularProgressIndicator(
                                color = AccentLavender, strokeWidth = 2.dp, modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                if (!communityViewModel.isLoading && posts.isEmpty() && communityViewModel.feedError == null) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🌱", fontSize = 40.sp)
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "Be the first to share something",
                                    fontSize = 14.sp, color = TextSecondary, textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                communityViewModel.feedError?.let { _ ->
                    item {
                        Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Could not load feed", fontSize = 13.sp, color = TextSecondary)
                                Spacer(Modifier.height(8.dp))
                                TextButton(onClick = { communityViewModel.loadFeed(refresh = true) }) {
                                    Text("Retry", color = AccentLavender)
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
                    CircularProgressIndicator(
                        color = AccentLavender, strokeWidth = 2.dp, modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    // ── Full-screen Story Viewer (Instagram/WhatsApp style) ───────────────────
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
                    // Text-only story: gradient background
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(listOf(Color(0xFF1A0A3C), Color(0xFF3D1A7A), Color(0xFF1A0A3C)))
                            )
                    )
                }

                // Top gradient overlay (for readability of header)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(listOf(Color.Black.copy(0.75f), Color.Transparent))
                        )
                )

                // Bottom gradient overlay (for caption readability)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.85f)))
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
                        // Avatar bubble
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
                                story.createdAt.toRelativeTime(),
                                fontSize = 11.sp, color = Color.White.copy(0.7f)
                            )
                        }
                        // Close button
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
                    // View count badge
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
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.18f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Add to Story", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        IconButton(
                            onClick = { showStoryDialog = false; storyText = ""; selectedStoryImageUri = null },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(Modifier.height(12.dp))

                    if (selectedStoryImageUri == null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF12103A), RoundedCornerShape(12.dp))
                                .border(1.dp, PrimaryPurple.copy(0.25f), RoundedCornerShape(12.dp))
                                .clickable {
                                    storyImagePicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .padding(horizontal = 14.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, null, tint = AccentLavender, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Add Photo (optional)", fontSize = 13.sp, color = AccentLavender, fontWeight = FontWeight.Medium)
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
                        placeholder = { Text("What's on your mind?", color = TextHint, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentLavender, unfocusedBorderColor = PrimaryPurple.copy(0.3f),
                            cursorColor = AccentLavender,
                            focusedContainerColor = Color(0xFF12103A), unfocusedContainerColor = Color(0xFF12103A)
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
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                    ) {
                        if (communityViewModel.isUploading) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                        } else {
                            Text("Share Story ✨", color = Color.White, fontWeight = FontWeight.SemiBold)
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
            containerColor = CardSurface,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .background(TextHint, RoundedCornerShape(2.dp))
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
                    "Comments", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                if (communityViewModel.commentsLoading) {
                    Box(Modifier.fillMaxWidth().height(80.dp), Alignment.Center) {
                        CircularProgressIndicator(color = AccentLavender, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                    }
                } else if (communityViewModel.comments.isEmpty()) {
                    Text(
                        "No comments yet. Be the first! 💬",
                        fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.height(300.dp)) {
                        items(communityViewModel.comments, key = { it.commentId }) { c ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                Box(
                                    modifier = Modifier.size(32.dp).background(Color(0xFF252248), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) { Text(c.userAvatar, fontSize = 14.sp) }
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(c.userName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    Text(c.text, fontSize = 13.sp, color = TextSecondary, lineHeight = 18.sp)
                                    Text(c.createdAt.toRelativeTime(), fontSize = 11.sp, color = TextHint)
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
                        placeholder = { Text("Add a comment…", color = TextHint, fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentLavender, unfocusedBorderColor = PrimaryPurple.copy(alpha = 0.3f),
                            cursorColor = AccentLavender,
                            focusedContainerColor = Color(0xFF12103A), unfocusedContainerColor = Color(0xFF12103A)
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
                            tint = if (commentText.isNotBlank()) AccentLavender else TextHint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }

    // ── New Post Dialog ───────────────────────────────────────────────────────
    if (showPostDialog) {
        val popularTags = listOf("motivation", "anxiety", "wellness", "healing", "mindfulness", "selfcare", "mentalhealth", "gratitude")
        Dialog(onDismissRequest = { dismissPostDialog() }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.18f))
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
                        Text("New Post", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        IconButton(onClick = { dismissPostDialog() }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newPostText,
                        onValueChange = { newPostText = it },
                        placeholder = { Text("What's on your mind?", color = TextHint, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentLavender, unfocusedBorderColor = PrimaryPurple.copy(alpha = 0.3f),
                            cursorColor = AccentLavender,
                            focusedContainerColor = Color(0xFF12103A), unfocusedContainerColor = Color(0xFF12103A)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (selectedPostImageUri == null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF12103A), RoundedCornerShape(12.dp))
                                .border(1.dp, PrimaryPurple.copy(0.25f), RoundedCornerShape(12.dp))
                                .clickable {
                                    postImagePicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Image, null, tint = AccentLavender, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Photo", fontSize = 13.sp, color = AccentLavender, fontWeight = FontWeight.Medium)
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

                    Text("Add Hashtags", fontSize = 12.sp, color = TextHint, fontWeight = FontWeight.Medium)
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
                            placeholder = { Text("wellness, anxiety…", fontSize = 12.sp, color = TextHint) },
                            prefix = { Text("#", color = AccentLavender, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                focusedBorderColor = AccentLavender, unfocusedBorderColor = PrimaryPurple.copy(0.3f),
                                cursorColor = AccentLavender, focusedContainerColor = Color(0xFF12103A),
                                unfocusedContainerColor = Color(0xFF12103A)
                            )
                        )
                        Button(
                            onClick = {
                                val tag = tagInput.trim().trimStart('#')
                                if (tag.isNotBlank() && !selectedPostTags.contains(tag)) selectedPostTags.add(tag)
                                tagInput = ""
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                            modifier = Modifier.height(52.dp)
                        ) { Text("Add", fontSize = 13.sp, color = Color.White) }
                    }

                    if (selectedPostTags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(selectedPostTags.toList()) { tag ->
                                Row(
                                    modifier = Modifier
                                        .background(PrimaryPurple.copy(0.22f), RoundedCornerShape(20.dp))
                                        .border(1.dp, AccentLavender.copy(0.35f), RoundedCornerShape(20.dp))
                                        .padding(start = 10.dp, end = 6.dp, top = 5.dp, bottom = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("#$tag", fontSize = 12.sp, color = AccentLavender)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.Close, null, tint = AccentLavender.copy(0.7f),
                                        modifier = Modifier.size(12.dp).clickable { selectedPostTags.remove(tag) }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Popular tags", fontSize = 11.sp, color = TextHint)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(popularTags) { tag ->
                            val isAdded = selectedPostTags.contains(tag)
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isAdded) PrimaryPurple.copy(0.28f) else Color(0xFF1E1B4A),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isAdded) AccentLavender.copy(0.5f) else PrimaryPurple.copy(0.2f),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { if (!isAdded) selectedPostTags.add(tag) else selectedPostTags.remove(tag) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text("#$tag", fontSize = 11.sp, color = if (isAdded) AccentLavender else TextSecondary)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(onClick = { dismissPostDialog() }, modifier = Modifier.weight(1f)) {
                            Text("Cancel", color = TextSecondary)
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
                                        onDone = { dismissPostDialog() }
                                    )
                                }
                            },
                            enabled = !communityViewModel.isUploading && newPostText.isNotBlank(),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                        ) {
                            if (communityViewModel.isUploading) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                            } else {
                                Text("Post 🚀", color = Color.White, fontWeight = FontWeight.SemiBold)
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
private fun LivePostCard(
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Brush.radialGradient(listOf(Color(0xFF3A2F7A), Color(0xFF1E1B4A))), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val av = post.userAvatar
                    if (av.isNotEmpty() && av.any { it.code > 127 }) {
                        Text(av, fontSize = 18.sp)
                    } else {
                        Icon(Icons.Default.Person, null, tint = AccentLavender, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.userName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(post.createdAt.toRelativeTime(), fontSize = 11.sp, color = TextHint)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(post.content, fontSize = 14.sp, color = TextPrimary, lineHeight = 21.sp)

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
                        .background(Color(0xFF1E1B3C), RoundedCornerShape(12.dp))
                )
            }

            if (post.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    post.tags.take(3).forEach { tag ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF2D2060), RoundedCornerShape(50.dp))
                                .border(1.dp, AccentLavender.copy(alpha = 0.2f), RoundedCornerShape(50.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) { Text(tag, color = AccentLavender, fontSize = 11.sp) }
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
                        tint = if (post.isLikedByMe) AccentLavender else TextSecondary,
                        modifier = Modifier.size(18.dp).scale(animScale)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(post.likeCount.toString(), fontSize = 13.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.width(22.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onComment() }) {
                    Icon(Icons.Default.ChatBubbleOutline, "Comment", tint = TextSecondary, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(post.commentCount.toString(), fontSize = 13.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.Send, "Share", tint = TextSecondary, modifier = Modifier.size(18.dp).clickable { })
                Spacer(modifier = Modifier.width(18.dp))
                Icon(
                    imageVector = if (post.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Save",
                    tint = if (post.isSaved) AccentLavender else TextSecondary,
                    modifier = Modifier.size(20.dp).clickable { onSave() }
                )
            }
        }
    }
}
