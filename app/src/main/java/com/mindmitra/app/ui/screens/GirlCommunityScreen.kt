package com.mindmitra.app.ui.screens

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mindmitra.app.ui.theme.GirlBg
import com.mindmitra.app.ui.theme.GirlBorder
import com.mindmitra.app.ui.theme.GirlCard
import com.mindmitra.app.ui.theme.GirlPrimary
import com.mindmitra.app.ui.theme.GirlPrimaryDim
import com.mindmitra.app.ui.theme.GirlSurface
import com.mindmitra.app.ui.theme.GirlTextDark
import com.mindmitra.app.ui.theme.GirlTextLight
import com.mindmitra.app.ui.theme.GirlTextMid

private data class GirlPost(
    val avatar: String,
    val name: String,
    val time: String,
    val content: String,
    val likes: Int,
    val liked: Boolean = false,
    val tag: String = "",
    val isSaved: Boolean = false,
    val hasImage: Boolean = false,
    val extraTags: List<String> = emptyList()
)

private data class GirlStoryUser(val avatar: String, val name: String, val hasStory: Boolean = true)

@Composable
fun GirlCommunityScreen(navController: NavController) {
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

    val posts = remember {
        mutableStateListOf(
            GirlPost("🌸", "Ananya", "2m ago", "Tried the breathing exercise today and it really helped with my anxiety. Feeling so much calmer now! 🌬️💕", 24, tag = "Wellness"),
            GirlPost("🌺", "Priya", "15m ago", "Reminder: You don't have to be perfect. You just have to be YOU. That's enough. 💗", 48, tag = "Motivation"),
            GirlPost("🌷", "Riya", "1h ago", "Today I wrote my first journal entry here. It felt so good to get all those feelings out. Thank you MindMitra 🙏", 31, tag = "Journal"),
            GirlPost("🌻", "Shreya", "3h ago", "Bad day today... but I know tomorrow will be better. One day at a time. 🌈", 19, tag = "Support"),
            GirlPost("🌼", "Kavya", "5h ago", "Does anyone else find the AI chat super helpful? It's like having someone who actually listens! 😊", 37, tag = "Chat"),
        )
    }
    var showPostDialog by remember { mutableStateOf(false) }
    var newPostText by remember { mutableStateOf("") }
    var tagInput by remember { mutableStateOf("") }
    val selectedPostTags = remember { mutableStateListOf<String>() }
    var hasSelectedImage by remember { mutableStateOf(false) }

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(GirlBg)
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 16.dp)
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
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                // Pinned banner
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
                Spacer(modifier = Modifier.height(12.dp))
            }

            itemsIndexed(posts) { index, post ->
                GirlPostCard(
                    post = post,
                    onLike = {
                        posts[index] = post.copy(
                            liked = !post.liked,
                            likes = if (post.liked) post.likes - 1 else post.likes + 1
                        )
                    },
                    onSave = {
                        posts[index] = post.copy(isSaved = !post.isSaved)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

    // ── New Post Dialog ────────────────────────────────────────────────────────
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
                border = androidx.compose.foundation.BorderStroke(1.dp, GirlBorder)
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
                        Text("New Post 💕", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = GirlTextDark)
                        IconButton(onClick = { dismissDialog() }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, "Close", tint = GirlTextMid, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Caption input
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

                    // Image attachment
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
                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                        listOf(Color(0xFFFFE4EF), Color(0xFFFFF0F5))
                                    ),
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRect(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFFFCE4EC), Color(0xFFFFF0F5))))
                                drawCircle(
                                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                        listOf(Color(0x60EF5DA8), Color(0x20EF5DA8), Color.Transparent),
                                        center = Offset(size.width * 0.5f, size.height * 0.35f), radius = size.width * 0.35f
                                    ),
                                    radius = size.width * 0.35f, center = Offset(size.width * 0.5f, size.height * 0.35f)
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
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
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

                    // Tag input row
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

                    // Selected tag chips
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
                                    Icon(Icons.Default.Close, null, tint = GirlPrimary.copy(0.6f),
                                        modifier = Modifier.size(12.dp).clickable { selectedPostTags.remove(tag) })
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Popular tag suggestions
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
                                    .border(1.dp, if (isAdded) GirlBorder else GirlBorder.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                    .clickable { if (!isAdded) selectedPostTags.add(tag) else selectedPostTags.remove(tag) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    "#$tag",
                                    fontSize = 11.sp,
                                    color = if (isAdded) GirlPrimary else GirlTextMid
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
                        ) { Text("Cancel", color = GirlTextMid) }

                        Button(
                            onClick = {
                                val text = newPostText.trim()
                                if (text.isNotEmpty()) {
                                    posts.add(0, GirlPost(
                                        avatar = "💗", name = "You", time = "Just now",
                                        content = text, likes = 0,
                                        hasImage = hasSelectedImage,
                                        extraTags = selectedPostTags.toList()
                                    ))
                                    dismissDialog()
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

@Composable
private fun GirlPostCard(post: GirlPost, onLike: () -> Unit, onSave: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GirlCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GirlBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(42.dp).background(GirlPrimaryDim, CircleShape)
                        .border(2.dp, GirlBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(post.avatar, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = GirlTextDark)
                    Text(post.time, fontSize = 11.sp, color = GirlTextLight)
                }
                if (post.tag.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .background(GirlPrimaryDim, RoundedCornerShape(20.dp))
                            .border(1.dp, GirlBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(post.tag, fontSize = 11.sp, color = GirlPrimary, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(post.content, fontSize = 14.sp, color = GirlTextDark, lineHeight = 21.sp)

            // Image placeholder (if post has image)
            if (post.hasImage) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(140.dp)
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFFFCE4EC), Color(0xFFFFF0F5))),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌸", fontSize = 40.sp)
                }
            }

            // Extra hashtag chips
            if (post.extraTags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(post.extraTags) { tag ->
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Like
                Row(
                    modifier = Modifier.clickable { onLike() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (post.liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.liked) GirlPrimary else GirlTextLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${post.likes}", fontSize = 13.sp, color = if (post.liked) GirlPrimary else GirlTextMid)
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Comment
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ModeComment, "Comment", tint = GirlTextLight, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reply", fontSize = 13.sp, color = GirlTextMid)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Share
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Share",
                    tint = GirlTextLight,
                    modifier = Modifier.size(18.dp).clickable { }
                )

                Spacer(modifier = Modifier.width(18.dp))

                // Bookmark (stateful)
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
