package com.mindmitra.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mindmitra.app.ui.theme.AccentLavender
import com.mindmitra.app.ui.theme.CardSurface
import com.mindmitra.app.ui.theme.DeepNavy
import com.mindmitra.app.ui.theme.PrimaryPurple
import com.mindmitra.app.ui.theme.TextHint
import com.mindmitra.app.ui.theme.TextPrimary
import com.mindmitra.app.ui.theme.TextSecondary
import androidx.compose.foundation.Canvas

private enum class MoodExpression { HAPPY, NEUTRAL, SAD }

private data class MoodOption(
    val label: String,
    val bgColor: Color,
    val blobColor: Color,
    val expression: MoodExpression
)

@Composable
fun MoodTrackerScreen(navController: NavController) {
    var selectedMood by remember { mutableStateOf<String?>(null) }
    var note by remember { mutableStateOf("") }

    val moods = listOf(
        MoodOption("Amazing",     Color(0xFF0E2E29), Color(0xFF2DD4BF), MoodExpression.HAPPY),
        MoodOption("Good",        Color(0xFF112610), Color(0xFF4ADE80), MoodExpression.HAPPY),
        MoodOption("Okay",        Color(0xFF252010), Color(0xFFFBBF24), MoodExpression.NEUTRAL),
        MoodOption("Sad",         Color(0xFF0F1B38), Color(0xFF60A5FA), MoodExpression.SAD),
        MoodOption("Anxious",     Color(0xFF1C1040), Color(0xFFA78BFA), MoodExpression.SAD),
        MoodOption("Angry",       Color(0xFF350E0E), Color(0xFFFF6B6B), MoodExpression.SAD),
        MoodOption("Tired",       Color(0xFF181828), Color(0xFF94A3B8), MoodExpression.NEUTRAL),
        MoodOption("Stressed",    Color(0xFF281600), Color(0xFFFB923C), MoodExpression.SAD),
        MoodOption("Overwhelmed", Color(0xFF180828), Color(0xFFC084FC), MoodExpression.SAD),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar with back button
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(CardSurface, CircleShape)
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Title
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "How are you\nfeeling right now?",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your feelings matter",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 3×3 mood grid (using Row/Column to avoid nested scroll)
        val rows = moods.chunked(3)
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            rows.forEach { rowMoods ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowMoods.forEach { mood ->
                        MoodItem(
                            mood = mood,
                            isSelected = selectedMood == mood.label,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedMood = mood.label }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Optional note
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Add a note ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(text = "(Optional)", fontSize = 13.sp, color = TextSecondary)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = TextHint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = {
                    Text(
                        text = "Write what you're feeling...",
                        color = TextHint,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = TextHint.copy(alpha = 0.25f),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = AccentLavender,
                    focusedContainerColor = CardSurface,
                    unfocusedContainerColor = CardSurface,
                ),
                maxLines = 3
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Save button
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
        ) {
            Text(
                text = "Save My Mood",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}

@Composable
private fun MoodItem(
    mood: MoodOption,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(mood.bgColor, RoundedCornerShape(18.dp))
            .border(
                width = if (isSelected) 2.dp else 0.8.dp,
                color = if (isSelected) mood.blobColor else mood.blobColor.copy(alpha = 0.22f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(modifier = Modifier.size(52.dp)) {
                drawMoodBlob(mood.blobColor, mood.expression, isSelected)
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = mood.label,
                fontSize = 11.sp,
                color = TextPrimary,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun DrawScope.drawMoodBlob(color: Color, expression: MoodExpression, isSelected: Boolean) {
    val cx = size.width / 2f
    val cy = size.height / 2f - 1f
    val r = size.minDimension * 0.40f

    // Selection glow
    if (isSelected) {
        drawCircle(color = color.copy(alpha = 0.22f), radius = r * 1.35f, center = Offset(cx, cy))
    }

    // Blob body (slightly oval — taller than wide like a ghost)
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.95f), color.copy(alpha = 0.72f)),
            center = Offset(cx, cy - r * 0.08f),
            radius = r
        ),
        topLeft = Offset(cx - r * 0.80f, cy - r * 0.75f),
        size = Size(r * 1.60f, r * 1.52f)
    )

    // Eyes (white base + dark pupil)
    val eyeY = cy - r * 0.10f
    drawCircle(Color.White, radius = 4.2f, center = Offset(cx - r * 0.27f, eyeY))
    drawCircle(Color.White, radius = 4.2f, center = Offset(cx + r * 0.27f, eyeY))
    drawCircle(Color(0xFF0F0B2A), radius = 2.4f, center = Offset(cx - r * 0.25f, eyeY + 0.8f))
    drawCircle(Color(0xFF0F0B2A), radius = 2.4f, center = Offset(cx + r * 0.25f, eyeY + 0.8f))

    // Mouth
    val mouthY = cy + r * 0.20f
    val mouthW = r * 0.54f
    val mouthH = r * 0.22f

    when (expression) {
        MoodExpression.HAPPY -> {
            // Smile ∪
            drawArc(
                color = Color(0xFF0F0B2A),
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(cx - mouthW / 2f, mouthY),
                size = Size(mouthW, mouthH),
                style = Stroke(width = 2.5f, cap = StrokeCap.Round)
            )
        }
        MoodExpression.NEUTRAL -> {
            // Straight line
            drawLine(
                color = Color(0xFF0F0B2A),
                start = Offset(cx - mouthW * 0.38f, mouthY + mouthH / 2f),
                end = Offset(cx + mouthW * 0.38f, mouthY + mouthH / 2f),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round
            )
        }
        MoodExpression.SAD -> {
            // Frown ∩
            drawArc(
                color = Color(0xFF0F0B2A),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(cx - mouthW / 2f, mouthY - mouthH),
                size = Size(mouthW, mouthH),
                style = Stroke(width = 2.5f, cap = StrokeCap.Round)
            )
        }
    }
}
