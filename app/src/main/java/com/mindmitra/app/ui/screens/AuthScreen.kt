package com.mindmitra.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindmitra.app.ui.theme.AccentLavender
import com.mindmitra.app.ui.theme.CardSurface
import com.mindmitra.app.ui.theme.DeepNavy
import com.mindmitra.app.ui.theme.PrimaryPurple
import com.mindmitra.app.ui.theme.TextHint
import com.mindmitra.app.ui.theme.TextPrimary
import com.mindmitra.app.ui.theme.TextSecondary
import com.mindmitra.app.ui.viewmodel.AuthViewModel
import com.mindmitra.app.ui.viewmodel.UserViewModel

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    onNavigateMale: () -> Unit,
    onNavigateFemale: () -> Unit
) {
    var isSignUp by remember { mutableStateOf(true) }

    // Sign-in state
    var loginEmail    by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginError    by remember { mutableStateOf("") }
    var loginPwdVisible by remember { mutableStateOf(false) }

    // Sign-up state
    var signName      by remember { mutableStateOf("") }
    var signEmail     by remember { mutableStateOf("") }
    var signPassword  by remember { mutableStateOf("") }
    var signGender    by remember { mutableStateOf("") }  // "Male" or "Female"
    var signError     by remember { mutableStateOf("") }
    var signPwdVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // ── Brand ─────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(
                    Brush.radialGradient(listOf(Color(0xFF7C5CE7), Color(0xFF4A3080))),
                    CircleShape
                )
                .border(2.dp, AccentLavender.copy(0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.SmartToy, null, tint = Color.White, modifier = Modifier.size(38.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("MindMitra", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Your AI Companion for Mental Wellness", fontSize = 13.sp, color = TextSecondary,
            textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))

        Spacer(modifier = Modifier.height(28.dp))

        // ── Tab toggle ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .background(CardSurface, RoundedCornerShape(30.dp))
                .border(1.dp, PrimaryPurple.copy(0.25f), RoundedCornerShape(30.dp))
                .padding(4.dp)
        ) {
            listOf("Create Account" to true, "Sign In" to false).forEach { (label, mode) ->
                Box(
                    modifier = Modifier
                        .background(
                            if (isSignUp == mode) PrimaryPurple else Color.Transparent,
                            RoundedCornerShape(26.dp)
                        )
                        .clickable { isSignUp = mode }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                        color = if (isSignUp == mode) Color.White else TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Form card ─────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(CardSurface, RoundedCornerShape(20.dp))
                .border(1.dp, PrimaryPurple.copy(0.2f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            if (isSignUp) {
                // ── Sign-up form ──────────────────────────────────────────────
                AuthField(
                    value = signName, onValueChange = { signName = it },
                    label = "Display Name", leadingIcon = Icons.Default.Person
                )
                Spacer(Modifier.height(12.dp))
                AuthField(
                    value = signEmail, onValueChange = { signEmail = it },
                    label = "Email", leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )
                Spacer(Modifier.height(12.dp))
                AuthField(
                    value = signPassword, onValueChange = { signPassword = it },
                    label = "Password", leadingIcon = Icons.Default.Lock,
                    isPassword = true, passwordVisible = signPwdVisible,
                    onTogglePassword = { signPwdVisible = !signPwdVisible }
                )
                Spacer(Modifier.height(20.dp))

                // Gender selection
                Text("I am a...", fontSize = 13.sp, color = TextSecondary,
                    fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf("Male" to "🧑", "Female" to "👧").forEach { (gender, emoji) ->
                        val selected = signGender == gender
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (selected) PrimaryPurple.copy(0.2f) else Color(0xFF12103A),
                                    RoundedCornerShape(14.dp)
                                )
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) AccentLavender else PrimaryPurple.copy(0.3f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { signGender = gender }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(emoji, fontSize = 28.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    gender, fontSize = 13.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) AccentLavender else TextSecondary
                                )
                            }
                        }
                    }
                }

                if (signError.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(signError, fontSize = 12.sp, color = Color(0xFFFF6B6B))
                }

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        when {
                            signName.isBlank()      -> signError = "Please enter your name"
                            signEmail.isBlank()     -> signError = "Please enter your email"
                            signPassword.length < 6 -> signError = "Password must be 6+ characters"
                            signGender.isBlank()    -> signError = "Please select your gender"
                            else -> {
                                signError = ""
                                authViewModel.signup(
                                    signName, signEmail, signPassword, signGender,
                                    onSuccess = { isMale ->
                                        userViewModel.updateUserName(signName)
                                        if (isMale) onNavigateMale() else onNavigateFemale()
                                    },
                                    onError = { msg -> signError = msg }
                                )
                            }
                        }
                    },
                    enabled = !authViewModel.isLoading,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    if (authViewModel.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White, strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Create Account", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                            color = Color.White)
                    }
                }
            } else {
                // ── Sign-in form ──────────────────────────────────────────────
                AuthField(
                    value = loginEmail, onValueChange = { loginEmail = it },
                    label = "Email", leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )
                Spacer(Modifier.height(12.dp))
                AuthField(
                    value = loginPassword, onValueChange = { loginPassword = it },
                    label = "Password", leadingIcon = Icons.Default.Lock,
                    isPassword = true, passwordVisible = loginPwdVisible,
                    onTogglePassword = { loginPwdVisible = !loginPwdVisible }
                )

                if (loginError.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(loginError, fontSize = 12.sp, color = Color(0xFFFF6B6B))
                }

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        loginError = ""
                        authViewModel.login(
                            loginEmail.trim(), loginPassword,
                            onSuccess = { isMale ->
                                userViewModel.updateUserName(authViewModel.storedDisplayName())
                                if (isMale) onNavigateMale() else onNavigateFemale()
                            },
                            onError = { msg -> loginError = msg }
                        )
                    },
                    enabled = !authViewModel.isLoading,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    if (authViewModel.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White, strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Sign In", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = if (isSignUp) "Already have an account? Sign in" else "New here? Create account",
            fontSize = 13.sp,
            color = AccentLavender,
            modifier = Modifier.clickable { isSignUp = !isSignUp; signError = ""; loginError = "" }
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ─── Shared input field ───────────────────────────────────────────────────────

@Composable
private fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextHint) },
        leadingIcon = { Icon(leadingIcon, null, tint = AccentLavender, modifier = Modifier.size(20.dp)) },
        trailingIcon = if (isPassword) ({
            Icon(
                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                null, tint = TextHint, modifier = Modifier.size(20.dp).clickable { onTogglePassword() }
            )
        }) else null,
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = AccentLavender,
            unfocusedBorderColor = PrimaryPurple.copy(0.4f),
            cursorColor = AccentLavender,
            focusedContainerColor = Color(0xFF12103A),
            unfocusedContainerColor = Color(0xFF12103A)
        )
    )
}
