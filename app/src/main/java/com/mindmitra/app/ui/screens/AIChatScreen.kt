package com.mindmitra.app.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.util.Locale
import com.mindmitra.app.ui.theme.AccentLavender
import com.mindmitra.app.ui.theme.CardSurface
import com.mindmitra.app.ui.theme.DeepNavy
import com.mindmitra.app.ui.theme.PrimaryPurple
import com.mindmitra.app.ui.theme.TextHint
import com.mindmitra.app.ui.theme.TextPrimary
import com.mindmitra.app.ui.theme.TextSecondary
import com.mindmitra.app.ui.viewmodel.ChatMessage
import com.mindmitra.app.ui.viewmodel.ChatViewModel

@Composable
fun AIChatScreen(
    navController: NavController,
    chatViewModel: ChatViewModel = viewModel()
) {
    val messages = chatViewModel.messages
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }

    // Speech recognition launcher
    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListening = false
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull() ?: ""
            if (spoken.isNotBlank()) inputText = spoken
        }
    }

    fun launchSpeech() {
        isListening = true
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to MindMitra…")
        }
        speechLauncher.launch(intent)
    }

    // Auto-scroll to latest message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
        ChatHeader(navController = navController)

        // Messages list — takes all remaining vertical space
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                when {
                    message.isTyping -> TypingIndicator()
                    message.isUser  -> UserBubble(message.text)
                    else -> {
                        BotBubble(text = message.text, isCrisis = message.isCrisis)
                        if (message.showQuickReplies && message.quickReplies.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            QuickReplies(
                                options = message.quickReplies,
                                onSelect = { chatViewModel.sendMessage(it) }
                            )
                        }
                    }
                }
            }
        }

        // Input bar — sticks to bottom, rises with keyboard
        ChatInputBar(
            value = inputText,
            onValueChange = { inputText = it },
            onSend = {
                val text = inputText.trim()
                if (text.isNotEmpty()) {
                    chatViewModel.sendMessage(text)
                    inputText = ""
                }
            },
            onMicClick = { launchSpeech() },
            isListening = isListening
        )
    }
}

// ─── Header ──────────────────────────────────────────────────────────────────

@Composable
private fun ChatHeader(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepNavy)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = TextPrimary,
            modifier = Modifier
                .size(24.dp)
                .clickable { navController.popBackStack() }
        )

        Spacer(modifier = Modifier.width(14.dp))

        // Bot avatar
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(Color(0xFF2D2060), CircleShape)
                .border(2.dp, AccentLavender.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = null,
                tint = AccentLavender,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "AI Chat",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Canvas(modifier = Modifier.size(7.dp)) {
                    drawCircle(Color(0xFF4ADE80), radius = size.minDimension / 2f)
                }
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Your emotional support buddy ✨",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

// ─── Bot bubble ───────────────────────────────────────────────────────────────

@Composable
private fun BotBubble(text: String, isCrisis: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFF2D2060), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = null,
                tint = AccentLavender,
                modifier = Modifier.size(17.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .widthIn(max = 272.dp)
                .background(
                    color = if (isCrisis) Color(0xFF2A1020) else Color(0xFF1E1B3C),
                    shape = RoundedCornerShape(
                        topStart = 18.dp, topEnd = 18.dp,
                        bottomStart = 4.dp, bottomEnd = 18.dp
                    )
                )
                .then(
                    if (isCrisis) Modifier.border(
                        1.dp,
                        Color(0xFFFF6B6B).copy(alpha = 0.45f),
                        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp)
                    ) else Modifier
                )
                .padding(horizontal = 14.dp, vertical = 11.dp)
        ) {
            Text(
                text = text,
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )
        }
    }
}

// ─── User bubble ──────────────────────────────────────────────────────────────

@Composable
private fun UserBubble(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 272.dp)
                .background(
                    color = PrimaryPurple,
                    shape = RoundedCornerShape(
                        topStart = 18.dp, topEnd = 18.dp,
                        bottomStart = 18.dp, bottomEnd = 4.dp
                    )
                )
                .padding(horizontal = 14.dp, vertical = 11.dp)
        ) {
            Text(
                text = text,
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )
        }
    }
}

// ─── Animated typing indicator (3 pulsing dots) ───────────────────────────────

@Composable
private fun TypingIndicator() {
    val transition = rememberInfiniteTransition(label = "typing")

    val dot1 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(500, easing = LinearEasing), RepeatMode.Reverse
        ), label = "d1"
    )
    val dot2 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(500, delayMillis = 150, easing = LinearEasing), RepeatMode.Reverse
        ), label = "d2"
    )
    val dot3 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(500, delayMillis = 300, easing = LinearEasing), RepeatMode.Reverse
        ), label = "d3"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFF2D2060), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.SmartToy, null, tint = AccentLavender, modifier = Modifier.size(17.dp))
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .background(
                    Color(0xFF1E1B3C),
                    RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp)
                )
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Canvas(modifier = Modifier.size(8.dp)) {
                    drawCircle(AccentLavender.copy(alpha = dot1), radius = size.minDimension / 2f)
                }
                Canvas(modifier = Modifier.size(8.dp)) {
                    drawCircle(AccentLavender.copy(alpha = dot2), radius = size.minDimension / 2f)
                }
                Canvas(modifier = Modifier.size(8.dp)) {
                    drawCircle(AccentLavender.copy(alpha = dot3), radius = size.minDimension / 2f)
                }
            }
        }
    }
}

// ─── Quick reply pills ────────────────────────────────────────────────────────

@Composable
private fun QuickReplies(options: List<String>, onSelect: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(start = 40.dp, end = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options) { option ->
            Box(
                modifier = Modifier
                    .border(
                        width = 1.5.dp,
                        color = AccentLavender.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(50.dp)
                    )
                    .clickable { onSelect(option) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = option,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ─── Input bar ────────────────────────────────────────────────────────────────

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onMicClick: () -> Unit = {},
    isListening: Boolean = false
) {
    // Pulsing glow when mic is active
    val transition = rememberInfiniteTransition(label = "mic")
    val micPulse by transition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "micPulse"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F0D22))
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .navigationBarsPadding()
            .imePadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mic button
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(
                    if (isListening) AccentLavender.copy(alpha = micPulse * 0.35f)
                    else Color(0xFF1E1B3C),
                    CircleShape
                )
                .border(
                    1.5.dp,
                    if (isListening) AccentLavender.copy(alpha = micPulse)
                    else AccentLavender.copy(alpha = 0.25f),
                    CircleShape
                )
                .clickable { onMicClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice input",
                tint = if (isListening) AccentLavender else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Text input
        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = if (isListening) "Listening…" else "Type or speak…",
                    color = if (isListening) AccentLavender.copy(0.7f) else TextHint,
                    fontSize = 14.sp
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                cursorBrush = SolidColor(AccentLavender),
                maxLines = 4
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Send button
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(
                    if (value.isNotBlank()) PrimaryPurple else Color(0xFF1E1B3C),
                    CircleShape
                )
                .clickable { if (value.isNotBlank()) onSend() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send",
                tint = if (value.isNotBlank()) TextPrimary else TextHint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
