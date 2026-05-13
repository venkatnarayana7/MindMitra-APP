package com.mindmitra.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mindmitra.app.navigation.Routes
import com.mindmitra.app.ui.theme.AccentLavender
import com.mindmitra.app.ui.theme.BottomNavBg
import com.mindmitra.app.ui.theme.CardSurface
import com.mindmitra.app.ui.theme.DeepNavy
import com.mindmitra.app.ui.theme.PrimaryPurple
import com.mindmitra.app.ui.theme.TextHint
import com.mindmitra.app.ui.theme.TextPrimary
import com.mindmitra.app.ui.theme.TextSecondary

private data class CommunityPost(
    val id: Int,
    val timeAgo: String,
    val content: String,
    val tags: List<String> = emptyList(),
    val likes: Int = 0,
    val comments: Int = 0,
    val hasImage: Boolean = false,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false
)

private data class StoryUser(val avatar: String, val name: String, val hasStory: Boolean = true)

@Composable
fun CommunityScreen(navController: NavController) {
    var showPostDialog by remember { mutableStateOf(false) }
    var newPostText by remember { mutableStateOf("") }
    var tagInput by remember { mutableStateOf("") }
    val selectedPostTags = remember { mutableStateListOf<String>() }
    var hasSelectedImage by remember { mutableStateOf(false) }

    val stories = remember {
        listOf(
            StoryUser("🧠", "Your\nStory", false),
            StoryUser("🌟", "Arjun"),
            StoryUser("💡", "Meera"),
            StoryUser("🎯", "Rohan"),
            StoryUser("🌊", "Priya"),
            StoryUser("🔮", "Vikram"),
            StoryUser("🌈", "Divya"),
        )
    }

    val posts = remember {
        mutableStateListOf(
            CommunityPost(
                id = 1,
                timeAgo = "3 hours ago",
                content = "Some days are just hard. And that's okay. You're stronger than you think. 🌸",
                tags = listOf("Life", "Motivation"),
                likes = 124,
                comments = 32
            ),
            CommunityPost(
                id = 2,
                timeAgo = "3 hours ago",
                content = "Grateful for the small things that make life better.",
                hasImage = true,
                likes = 89,
                comments = 14
            ),
            CommunityPost(
                id = 3,
                timeAgo = "5 hours ago",
                content = "Taking things one breath at a time. Anxiety is real, but so is your strength. 💙",
                tags = listOf("Anxiety", "Support"),
                likes = 201,
                comments = 47
            ),
            CommunityPost(
                id = 4,
                timeAgo = "Yesterday",
                content = "Reminder: it's okay to not be okay. Healing isn't linear. Be patient with yourself. ✨",
                tags = listOf("Healing"),
                likes = 315,
                comments = 68
            ),
        )
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DeepNavy)
                .statusBarsPadding(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stories row
            item {
                    LazyRow(
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
                                                listOf(Color(0xFF6C5CE7), Color(0xFF9B8BFA), Color(0xFF6C5CE7))
                                            ) else Brush.radialGradient(listOf(Color(0xFF2A2850), Color(0xFF1E1B4A))),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(if (story.hasStory) 48.dp else 54.dp)
                                            .background(Color(0xFF1E1B4A), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(story.avatar, fontSize = 22.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    story.name,
                                    fontSize = 10.sp, color = TextSecondary,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 13.sp,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(posts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        onLike = {
                            val index = posts.indexOf(post)
                            if (index >= 0) {
                                posts[index] = post.copy(
                                    isLiked = !post.isLiked,
                                    likes = if (post.isLiked) post.likes - 1 else post.likes + 1
                                )
                            }
                        },
                        onSave = {
                            val index = posts.indexOf(post)
                            if (index >= 0) {
                                posts[index] = post.copy(isSaved = !post.isSaved)
                            }
                        }
                    )
                }
        }
    }

    // ── New Post Dialog ────────────────────────────────────────────────────────
    if (showPostDialog) {
        val popularTags = listOf("motivation", "anxiety", "wellness", "healing", "mindfulness", "selfcare", "mentalhealth", "gratitude")
        fun dismissDialog() {
            showPostDialog = false; newPostText = ""; tagInput = ""
            selectedPostTags.clear(); hasSelectedImage = false
        }
        Dialog(onDismissRequest = { dismissDialog() }) {
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
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("New Post", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        IconButton(onClick = { dismissDialog() }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Caption input
                    OutlinedTextField(
                        value = newPostText,
                        onValueChange = { newPostText = it },
                        placeholder = { Text("What's on your mind?", color = TextHint, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentLavender,
                            unfocusedBorderColor = PrimaryPurple.copy(alpha = 0.3f),
                            cursorColor = AccentLavender,
                            focusedContainerColor = Color(0xFF12103A),
                            unfocusedContainerColor = Color(0xFF12103A)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Image attachment area
                    if (!hasSelectedImage) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF12103A), RoundedCornerShape(12.dp))
                                .border(1.dp, PrimaryPurple.copy(0.25f), RoundedCornerShape(12.dp))
                                .clickable { hasSelectedImage = true }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Image, null, tint = AccentLavender, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Photo", fontSize = 13.sp, color = AccentLavender, fontWeight = FontWeight.Medium)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .background(
                                    Brush.verticalGradient(listOf(Color(0xFF1A1040), Color(0xFF0A0820))),
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRect(Brush.verticalGradient(listOf(Color(0xFF0A0820), Color(0xFF1A1040), Color(0xFF0D0820))))
                                drawCircle(
                                    brush = Brush.radialGradient(listOf(Color(0x60A090FF), Color(0x20604ABB), Color.Transparent),
                                        center = Offset(size.width * 0.5f, size.height * 0.4f), radius = size.width * 0.3f),
                                    radius = size.width * 0.3f, center = Offset(size.width * 0.5f, size.height * 0.4f)
                                )
                                val mp = Path().apply {
                                    val w = size.width; val h = size.height
                                    moveTo(0f, h); lineTo(0f, h * 0.7f); lineTo(w * 0.2f, h * 0.5f)
                                    lineTo(w * 0.4f, h * 0.65f); lineTo(w * 0.55f, h * 0.45f)
                                    lineTo(w * 0.75f, h * 0.6f); lineTo(w, h * 0.65f); lineTo(w, h); close()
                                }
                                drawPath(mp, Color(0xFF060412))
                                listOf(Offset(size.width * 0.15f, size.height * 0.15f), Offset(size.width * 0.35f, size.height * 0.1f),
                                    Offset(size.width * 0.65f, size.height * 0.12f), Offset(size.width * 0.82f, size.height * 0.2f)
                                ).forEach { drawCircle(Color(0x90FFFFFF), 1.8f, it) }
                            }
                            // Remove button
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color(0x99000000), RoundedCornerShape(8.dp))
                                    .clickable { hasSelectedImage = false }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("✕ Remove", fontSize = 11.sp, color = Color.White)
                            }
                            Text("🌄", fontSize = 32.sp, modifier = Modifier.align(Alignment.Center))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tag input row
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

                    // Selected tag chips
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
                                    Icon(Icons.Default.Close, null, tint = AccentLavender.copy(0.7f),
                                        modifier = Modifier.size(12.dp).clickable { selectedPostTags.remove(tag) })
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Popular tag suggestions
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
                                    .border(1.dp, if (isAdded) AccentLavender.copy(0.5f) else PrimaryPurple.copy(0.2f), RoundedCornerShape(20.dp))
                                    .clickable { if (!isAdded) selectedPostTags.add(tag) else selectedPostTags.remove(tag) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    "#$tag",
                                    fontSize = 11.sp,
                                    color = if (isAdded) AccentLavender else TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            onClick = { dismissDialog() },
                            modifier = Modifier.weight(1f)
                        ) { Text("Cancel", color = TextSecondary) }

                        Button(
                            onClick = {
                                val text = newPostText.trim()
                                if (text.isNotEmpty()) {
                                    val newId = (posts.maxOfOrNull { it.id } ?: 0) + 1
                                    posts.add(0, CommunityPost(
                                        id = newId, timeAgo = "Just now", content = text,
                                        tags = selectedPostTags.toList(), hasImage = hasSelectedImage
                                    ))
                                    dismissDialog()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                        ) {
                            Text("Post 🚀", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PostCard(post: CommunityPost, onLike: () -> Unit, onSave: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Author row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Anonymous avatar
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF3A2F7A), Color(0xFF1E1B4A))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = AccentLavender,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Anonymous",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = post.timeAgo,
                        fontSize = 11.sp,
                        color = TextHint
                    )
                }
                // Dismiss / more
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFF252248), CircleShape)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "×", color = TextHint, fontSize = 16.sp, lineHeight = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content text
            Text(
                text = post.content,
                fontSize = 14.sp,
                color = TextPrimary,
                lineHeight = 21.sp
            )

            // Post image placeholder
            if (post.hasImage) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(155.dp)
                        .background(Color(0xFF0F0B2A), RoundedCornerShape(12.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Dark sky gradient
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF0A0820), Color(0xFF1A1040), Color(0xFF0D0820))
                            )
                        )
                        // Glowing orb
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x60A090FF), Color(0x20604ABB), Color.Transparent),
                                center = Offset(size.width * 0.5f, size.height * 0.45f),
                                radius = size.width * 0.28f
                            ),
                            radius = size.width * 0.28f,
                            center = Offset(size.width * 0.5f, size.height * 0.45f)
                        )
                        // Mountain silhouette
                        val mountainPath = Path().apply {
                            val w = size.width; val h = size.height
                            moveTo(0f, h)
                            lineTo(0f, h * 0.72f)
                            lineTo(w * 0.15f, h * 0.55f)
                            lineTo(w * 0.30f, h * 0.68f)
                            lineTo(w * 0.45f, h * 0.48f)
                            lineTo(w * 0.62f, h * 0.65f)
                            lineTo(w * 0.78f, h * 0.52f)
                            lineTo(w, h * 0.68f)
                            lineTo(w, h)
                            close()
                        }
                        drawPath(mountainPath, Color(0xFF060412))
                        // Stars
                        listOf(
                            Offset(size.width * 0.12f, size.height * 0.15f),
                            Offset(size.width * 0.28f, size.height * 0.08f),
                            Offset(size.width * 0.55f, size.height * 0.12f),
                            Offset(size.width * 0.72f, size.height * 0.06f),
                            Offset(size.width * 0.88f, size.height * 0.18f),
                            Offset(size.width * 0.40f, size.height * 0.22f),
                        ).forEach { pos ->
                            drawCircle(Color(0x90FFFFFF), radius = 1.8f, center = pos)
                        }
                    }
                }
            }

            // Tags
            if (post.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    post.tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF2D2060), RoundedCornerShape(50.dp))
                                .border(1.dp, AccentLavender.copy(alpha = 0.2f), RoundedCornerShape(50.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(text = tag, color = AccentLavender, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Reaction row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLike() }
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) AccentLavender else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = post.likes.toString(),
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.width(22.dp))

                // Comment
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = TextSecondary,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = post.comments.toString(),
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Share
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Share",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp).clickable { }
                )

                Spacer(modifier = Modifier.width(18.dp))

                // Bookmark (stateful)
                Icon(
                    imageVector = if (post.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Save",
                    tint = if (post.isSaved) AccentLavender else TextSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onSave() }
                )
            }
        }
    }
}

@Composable
private fun CommunityBottomBar(navController: NavController) {
    val items = listOf(
        Triple("Home", Icons.Default.Home, Routes.HOME),
        Triple("Chat", Icons.Default.Chat, Routes.AI_CHAT),
        Triple("Journal",  Icons.Default.Book,     Routes.JOURNAL),
        Triple("Com", Icons.Default.Group, Routes.COMMUNITY),
        Triple("Profile", Icons.Default.Person, Routes.PROFILE),
    )
    NavigationBar(containerColor = BottomNavBg, tonalElevation = 0.dp) {
        items.forEachIndexed { index, (label, icon, route) ->
            val isSelected = index == 3 // Community tab
            NavigationBarItem(
                icon = { Icon(icon, label, modifier = Modifier.size(22.dp)) },
                label = { Text(label, fontSize = 11.sp) },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentLavender,
                    selectedTextColor = AccentLavender,
                    unselectedIconColor = TextHint,
                    unselectedTextColor = TextHint,
                    indicatorColor = PrimaryPurple.copy(alpha = 0.18f)
                )
            )
        }
    }
}
