package com.mindmitra.app.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mindmitra.app.data.ApiMessage
import com.mindmitra.app.data.GroqService
import com.mindmitra.app.data.chat.ChatDatabase
import com.mindmitra.app.data.chat.ChatHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ─── Message model ───────────────────────────────────────────────────────────

data class ChatMessage(
    val id: Long = System.nanoTime(),
    val text: String,
    val isUser: Boolean,
    val showQuickReplies: Boolean = false,
    val quickReplies: List<String> = emptyList(),
    val isTyping: Boolean = false,
    val isCrisis: Boolean = false
)

// ─── On-device crisis keyword filter ─────────────────────────────────────────

private val CRISIS_KEYWORDS = listOf(
    "suicide", "suicidal", "kill myself", "end my life", "end it all",
    "self harm", "self-harm", "hurt myself", "cut myself",
    "no reason to live", "don't want to live", "want to die",
    "can't go on", "can't take it anymore", "give up on life",
    "marna chahta", "marna chahti", "khud ko hurt", "jaan dena",
    "khud ko khatam", "jeena nahi chahta"
)

private const val SYSTEM_PROMPT = """You are MindMitra, a compassionate mental wellness companion built for Indian users. You speak with warmth, cultural sensitivity, and zero clinical jargon. You are not a therapist. You are a caring friend who understands modern Indian life pressures.

[IDENTITY]
Your name is MindMitra. You are warm, empathetic, non-judgmental, and deeply human. You never sound robotic or clinical. You sound like a trusted friend who truly listens.

[CULTURAL CONTEXT]
You deeply understand:
- Academic pressure: JEE, NEET, board exams, college placements, backlogs
- Workplace culture: IT sector appraisal anxiety, manager pressure, job insecurity
- Indian family dynamics: parental expectations, generational pressure, relationship obligations
- Social stigma around emotional wellbeing in India
- Multigenerational household stress, financial anxiety, marriage pressure
Reference these contexts naturally when the user mentions them.

[SAFETY RULES]
- Never diagnose any condition, ever.
- Never suggest or reference any medication.
- Never claim to replace a professional counselor or doctor.
- If a user asks a medical question, warmly redirect: "That's really important — a doctor or counselor would be the right person to help with that."
- If the user expresses that they are in immediate danger or a serious emotional emergency, encourage them to reach out to a trusted adult, a helpline, or emergency services immediately. Keep your response brief, warm, and direct in such cases.

[RESPONSE FORMAT]
- Warm, conversational, 80–150 words maximum. Never exceed this.
- Empathy FIRST — validate the feeling before offering anything else.
- End every response with EITHER a gentle open question OR ONE specific actionable micro-suggestion (a breathing exercise, a short walk, writing one sentence in a journal).
- Never lecture. Never bullet-point a list of advice. One thought at a time.
- No platitudes like "everything happens for a reason" or "stay positive".

[LANGUAGE RULES]
- Detect the language of the user's message automatically.
- English message → respond in English.
- Hindi message → respond in Hindi.
- Hinglish message → respond in Hinglish.
- Never switch languages unless the user switches first.

[SUGGESTION SIGNALS]
When you genuinely suggest a breathing exercise, meditation, or motivational reflection, add ONE of these tags on its own line at the very end of your response:
[TYPE:BREATHING]
[TYPE:MEDITATION]
[TYPE:MOTIVATIONAL]
Only include the tag when you are actually suggesting that activity — not on every message."""

// ─── ViewModel ────────────────────────────────────────────────────────────────

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val authPrefs = application.getSharedPreferences("mindmitra_auth", 0)
    private val chatPrefs = application.getSharedPreferences("mindmitra_prefs", 0)
    private val groqService = GroqService()
    private val db = ChatDatabase.get(application)

    val currentUserId: String get() = authPrefs.getString("userId", "local_user") ?: "local_user"
    val currentUserName: String get() = authPrefs.getString("display_name", chatPrefs.getString("user_name", "Friend") ?: "Friend") ?: "Friend"

    val messages = mutableStateListOf<ChatMessage>()
    var isLoading by mutableStateOf(false)
        private set
    var historyLoaded by mutableStateOf(false)
        private set

    // Full conversation history sent to Groq (includes system prompt + loaded context)
    private val apiHistory = mutableListOf(ApiMessage("system", SYSTEM_PROMPT))

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            val history = db.chatDao().getHistory(currentUserId)
            if (history.isEmpty()) {
                // Fresh account — show greeting
                messages.add(
                    ChatMessage(
                        text = "Hey $currentUserName 👋\nHow's your mind feeling today?",
                        isUser = false,
                        showQuickReplies = true,
                        quickReplies = listOf("I'm stressed", "I need motivation", "I'm doing okay")
                    )
                )
            } else {
                // Restore display history
                history.forEach { entity ->
                    messages.add(
                        ChatMessage(
                            id = entity.id,
                            text = entity.content,
                            isUser = entity.role == "user",
                            isCrisis = entity.role == "crisis"
                        )
                    )
                }
                // Load last 10 messages into Groq context for continuity
                history.takeLast(10)
                    .filter { it.role == "user" || it.role == "assistant" }
                    .forEach { apiHistory.add(ApiMessage(it.role, it.content)) }
            }
            historyLoaded = true
        }
    }

    fun clearHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            db.chatDao().clearHistory(currentUserId)
            messages.clear()
            apiHistory.clear()
            apiHistory.add(ApiMessage("system", SYSTEM_PROMPT))
            messages.add(
                ChatMessage(
                    text = "Hey $currentUserName 👋\nHow's your mind feeling today?",
                    isUser = false,
                    showQuickReplies = true,
                    quickReplies = listOf("I'm stressed", "I need motivation", "I'm doing okay")
                )
            )
        }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val lastBotIndex = messages.indexOfLast { !it.isUser && it.showQuickReplies }
        if (lastBotIndex >= 0) {
            messages[lastBotIndex] = messages[lastBotIndex].copy(
                showQuickReplies = false,
                quickReplies = emptyList()
            )
        }

        val userMsg = ChatMessage(text = userText, isUser = true)
        messages.add(userMsg)
        saveToDb(userText, "user")

        // ── On-device L1 crisis check ──────────────────────────────────────
        if (CRISIS_KEYWORDS.any { userText.lowercase().contains(it) }) {
            val crisisText = "I'm really glad you reached out. What you're feeling matters, and you don't have to face this alone. Please contact:\n\n📞 iCall (TISS): 9152987821\n📞 Vandrevala Foundation: 1860-2662-345\n\nThey're available 24/7 and will listen without judgment. Please reach out right now. 💙"
            messages.add(ChatMessage(text = crisisText, isUser = false, isCrisis = true))
            saveToDb(crisisText, "crisis")
            return
        }

        apiHistory.add(ApiMessage("user", userText))

        viewModelScope.launch {
            isLoading = true
            val typingMsg = ChatMessage(text = "", isUser = false, isTyping = true)
            messages.add(typingMsg)

            try {
                val rawResponse = groqService.chat(apiHistory)
                val cleanText = rawResponse
                    .replace(Regex("\\[TYPE:(BREATHING|MEDITATION|MOTIVATIONAL)]"), "")
                    .trim()
                apiHistory.add(ApiMessage("assistant", rawResponse))
                messages.removeIf { it.isTyping }
                messages.add(ChatMessage(text = cleanText, isUser = false))
                saveToDb(cleanText, "assistant")
            } catch (e: Exception) {
                messages.removeIf { it.isTyping }
                val userMsg2 = when {
                    e.message?.contains("401") == true ->
                        "API key expired or invalid. Get a new key at console.groq.com/keys 🔑"
                    e.message?.contains("429") == true ->
                        "Too many messages — please wait a moment and try again. ⏳"
                    e.message?.contains("model") == true || e.message?.contains("404") == true ->
                        "Model unavailable. Please try again later. 🤖"
                    e.message?.contains("timeout") == true || e.message?.contains("connect") == true ->
                        "No internet connection. Please check your network. 📶"
                    else -> "Something went wrong. Please try again."
                }
                messages.add(ChatMessage(text = userMsg2, isUser = false))
            } finally {
                isLoading = false
            }
        }
    }

    private fun saveToDb(content: String, role: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.chatDao().insert(ChatHistoryEntity(userId = currentUserId, role = role, content = content))
        }
    }
}
