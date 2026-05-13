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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.util.Locale
import com.mindmitra.app.ui.theme.GirlBg
import com.mindmitra.app.ui.theme.GirlBorder
import com.mindmitra.app.ui.theme.GirlCard
import com.mindmitra.app.ui.theme.GirlNavBg
import com.mindmitra.app.ui.theme.GirlPrimary
import com.mindmitra.app.ui.theme.GirlPrimaryDim
import com.mindmitra.app.ui.theme.GirlSurface
import com.mindmitra.app.ui.theme.GirlTextDark
import com.mindmitra.app.ui.theme.GirlTextLight
import com.mindmitra.app.ui.theme.GirlTextMid
import com.mindmitra.app.ui.viewmodel.ChatViewModel

@Composable
fun GirlChatScreen(navController: NavController) {
    val chatViewModel: ChatViewModel = viewModel()
    val messages = chatViewModel.messages
    var inputText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

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

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GirlBg)
            .imePadding()
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(GirlCard, CircleShape)
                    .border(1.dp, GirlBorder, CircleShape)
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, "Back", tint = GirlPrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(GirlPrimaryDim, CircleShape)
                    .border(2.dp, GirlBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.SmartToy, null, tint = GirlPrimary, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text("MindMitra AI", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GirlTextDark)
                Text("Your safe space 💗", fontSize = 12.sp, color = GirlTextMid)
            }
        }

        // Messages
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                when {
                    msg.isTyping -> GirlTypingIndicator()
                    msg.isUser -> GirlChatBubble(text = msg.text, isUser = true)
                    else -> GirlChatBubble(text = msg.text, isUser = false)
                }
            }
        }

        // Input bar — sticks to bottom, rises with keyboard
        val micTransition = rememberInfiniteTransition(label = "girlMic")
        val micPulse by micTransition.animateFloat(
            initialValue = 0.5f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(600, easing = LinearEasing), RepeatMode.Reverse),
            label = "girlMicPulse"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GirlNavBg)
                .border(1.dp, GirlBorder, RoundedCornerShape(0.dp))
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mic button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (isListening) GirlPrimary.copy(alpha = micPulse * 0.25f) else GirlPrimaryDim,
                        CircleShape
                    )
                    .border(
                        1.5.dp,
                        if (isListening) GirlPrimary.copy(alpha = micPulse) else GirlBorder,
                        CircleShape
                    )
                    .clickable { launchSpeech() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Mic, "Voice input",
                    tint = if (isListening) GirlPrimary else GirlTextMid,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Text input
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(GirlSurface, RoundedCornerShape(24.dp))
                    .border(1.dp, GirlBorder, RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                if (inputText.isEmpty()) {
                    Text(
                        text = if (isListening) "Listening…" else "Type or speak…",
                        fontSize = 14.sp,
                        color = if (isListening) GirlPrimary.copy(0.7f) else GirlTextLight
                    )
                }
                BasicTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    textStyle = TextStyle(fontSize = 14.sp, color = GirlTextDark),
                    cursorBrush = SolidColor(GirlPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Send button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(if (inputText.isNotBlank()) GirlPrimary else GirlPrimaryDim, CircleShape)
                    .clickable {
                        if (inputText.isNotBlank()) {
                            chatViewModel.sendMessage(inputText.trim())
                            inputText = ""
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Send, "Send",
                    tint = if (inputText.isNotBlank()) Color.White else GirlTextLight,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun GirlTypingIndicator() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(28.dp).background(GirlPrimaryDim, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.SmartToy, null, tint = GirlPrimary, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .background(GirlCard, RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                .border(1.dp, GirlBorder, RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text("Typing...", fontSize = 13.sp, color = GirlTextMid)
        }
    }
}

@Composable
private fun GirlChatBubble(text: String, isUser: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier.size(28.dp).background(GirlPrimaryDim, CircleShape).border(1.dp, GirlBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.SmartToy, null, tint = GirlPrimary, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(8.dp))
        }
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    if (isUser) GirlPrimary else GirlCard,
                    if (isUser) RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
                    else RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
                )
                .then(
                    if (!isUser) Modifier.border(1.dp, GirlBorder, RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                    else Modifier
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(text = text, fontSize = 14.sp, color = if (isUser) Color.White else GirlTextDark, lineHeight = 20.sp)
        }
    }
}
