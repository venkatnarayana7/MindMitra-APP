package com.mindmitra.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mindmitra.app.ui.screens.AIChatScreen
import com.mindmitra.app.ui.screens.AchievementsScreen
import com.mindmitra.app.ui.screens.StreakMapScreen
import com.mindmitra.app.ui.screens.AnalysisScreen
import com.mindmitra.app.ui.screens.AuthScreen
import com.mindmitra.app.ui.screens.BoyJournalScreen
import com.mindmitra.app.ui.screens.BreathingScreen
import com.mindmitra.app.ui.screens.CommunityScreen
import com.mindmitra.app.ui.screens.FriendsScreen
import com.mindmitra.app.ui.screens.GirlBreathingScreen
import com.mindmitra.app.ui.screens.GirlChatScreen
import com.mindmitra.app.ui.screens.GirlCommunityScreen
import com.mindmitra.app.ui.screens.GirlFriendsScreen
import com.mindmitra.app.ui.screens.GirlHomeScreen
import com.mindmitra.app.ui.screens.GirlJournalScreen
import com.mindmitra.app.ui.screens.GirlMoodTrackerScreen
import com.mindmitra.app.ui.screens.GirlProfileScreen
import com.mindmitra.app.ui.screens.GirlSettingsScreen
import com.mindmitra.app.ui.screens.HomeScreen
import com.mindmitra.app.ui.screens.MoodTrackerScreen
import com.mindmitra.app.ui.screens.ProfileScreen
import com.mindmitra.app.ui.screens.SettingsScreen
import com.mindmitra.app.ui.screens.SplashScreen
import com.mindmitra.app.ui.viewmodel.AuthViewModel
import com.mindmitra.app.ui.viewmodel.UserViewModel

object Routes {
    const val SPLASH        = "splash"
    const val AUTH          = "auth"

    // Boy (dark purple) app
    const val HOME          = "home"
    const val MOOD_TRACKER  = "mood_tracker"
    const val AI_CHAT       = "ai_chat"
    const val ANALYSIS      = "analysis"
    const val JOURNAL       = "journal"
    const val COMMUNITY     = "community"
    const val PROFILE       = "profile"
    const val SETTINGS      = "settings"
    const val BREATHING     = "breathing"
    const val ACHIEVEMENTS  = "achievements"
    const val STREAK_MAP    = "streak_map"
    const val FRIENDS       = "friends"

    // Girl (pink) app
    const val GIRL_HOME         = "girl_home"
    const val GIRL_CHAT         = "girl_chat"
    const val GIRL_MOOD_TRACKER = "girl_mood_tracker"
    const val GIRL_JOURNAL      = "girl_journal"
    const val GIRL_COMMUNITY    = "girl_community"
    const val GIRL_PROFILE      = "girl_profile"
    const val GIRL_SETTINGS     = "girl_settings"
    const val GIRL_BREATHING    = "girl_breathing"
    const val GIRL_FRIENDS      = "girl_friends"
}

@Composable
fun NavGraph(navController: NavHostController) {
    val userViewModel: UserViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                isFemale = authViewModel.isLoggedIn && !authViewModel.isMale,
                onNavigateToHome = {
                    when {
                        !authViewModel.isLoggedIn -> navController.navigate(Routes.AUTH) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                        authViewModel.isMale -> navController.navigate(Routes.HOME) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                        else -> navController.navigate(Routes.GIRL_HOME) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.AUTH) {
            AuthScreen(
                authViewModel = authViewModel,
                userViewModel = userViewModel,
                onNavigateMale = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                },
                onNavigateFemale = {
                    navController.navigate(Routes.GIRL_HOME) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        // ── Boy app ──────────────────────────────────────────────────────────

        composable(Routes.HOME) {
            HomeScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(Routes.MOOD_TRACKER) {
            MoodTrackerScreen(navController = navController)
        }

        composable(Routes.AI_CHAT) {
            AIChatScreen(navController = navController)
        }

        composable(Routes.COMMUNITY) {
            CommunityScreen(navController = navController)
        }

        composable(Routes.ANALYSIS) {
            AnalysisScreen(navController = navController)
        }

        composable(Routes.PROFILE) {
            ProfileScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(Routes.BREATHING) {
            BreathingScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(Routes.JOURNAL) {
            BoyJournalScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(Routes.ACHIEVEMENTS) {
            AchievementsScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(Routes.STREAK_MAP) {
            StreakMapScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(Routes.FRIENDS) {
            FriendsScreen(navController = navController)
        }

        // ── Girl app ─────────────────────────────────────────────────────────

        composable(Routes.GIRL_HOME) {
            GirlHomeScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(Routes.GIRL_CHAT) {
            GirlChatScreen(navController = navController)
        }

        composable(Routes.GIRL_MOOD_TRACKER) {
            GirlMoodTrackerScreen(navController = navController)
        }

        composable(Routes.GIRL_JOURNAL) {
            GirlJournalScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(Routes.GIRL_COMMUNITY) {
            GirlCommunityScreen(navController = navController)
        }

        composable(Routes.GIRL_PROFILE) {
            GirlProfileScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(Routes.GIRL_SETTINGS) {
            GirlSettingsScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(Routes.GIRL_BREATHING) {
            GirlBreathingScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(Routes.GIRL_FRIENDS) {
            GirlFriendsScreen(navController = navController)
        }
    }
}
