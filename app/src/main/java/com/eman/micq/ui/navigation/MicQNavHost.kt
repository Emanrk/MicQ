package com.eman.micq.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.eman.micq.ui.auth.LoginScreen
import com.eman.micq.ui.auth.RegisterScreen
import com.eman.micq.ui.auth.RoleSelectionScreen
import com.eman.micq.ui.dashboards.AdminDashboardScreen
import com.eman.micq.ui.dashboards.PerformerDashboardScreen
import com.eman.micq.ui.dashboards.ShiftHistoryScreen
import com.eman.micq.ui.dashboards.SongHistoryScreen
import com.eman.micq.ui.dj.DjDashboardScreen
import com.eman.micq.ui.dj.DjShiftScreen
import com.eman.micq.ui.onboarding.OnboardingScreen
import com.eman.micq.ui.onboarding.SplashScreen
import com.eman.micq.ui.performer.AddToQueueScreen
import com.eman.micq.viewmodel.AdminActivityViewModel
import com.eman.micq.viewmodel.AuthViewModel
import com.eman.micq.viewmodel.DjViewModel
import com.eman.micq.viewmodel.QueueViewModel
import com.eman.micq.viewmodel.ShiftHistoryViewModel
import com.eman.micq.viewmodel.SongHistoryViewModel

@Composable
fun MicQNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingFinished = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.RoleSelection.route)
                },
                onLoginSuccess = { role ->
                    val destination = when (role) {
                        "ADMIN" -> Screen.AdminDashboard.route
                        "DJ" -> Screen.DjShift.route
                        "PERFORMER" -> Screen.PerformerDashboard.route
                        else -> Screen.Login.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(
                onRoleSelected = { role ->
                    navController.navigate(Screen.Register.createRoute(role))
                }
            )
        }

        composable(Screen.Register.route) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "PERFORMER"
            RegisterScreen(
                role = role,
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onRegistrationSuccess = {
                    val destination = when (role) {
                        "ADMIN" -> Screen.AdminDashboard.route
                        "DJ" -> Screen.DjShift.route
                        "PERFORMER" -> Screen.PerformerDashboard.route
                        else -> Screen.Login.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.RoleSelection.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AdminDashboard.route) {
            val adminViewModel: AdminActivityViewModel = hiltViewModel()
            AdminDashboardScreen(
                viewModel = adminViewModel,
                onNavigateToShiftHistory = { navController.navigate(Screen.ShiftHistory.route) },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Screen.ShiftHistory.route) {
            val shiftHistoryViewModel: ShiftHistoryViewModel = hiltViewModel()
            ShiftHistoryScreen(
                viewModel = shiftHistoryViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SongHistory.route) {
            val songHistoryViewModel: SongHistoryViewModel = hiltViewModel()
            SongHistoryScreen(
                viewModel = songHistoryViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PerformerDashboard.route) {
            PerformerDashboardScreen(
                onNavigateToAddQueue = { navController.navigate(Screen.AddToQueue.route) },
                onNavigateToSongHistory = { navController.navigate(Screen.SongHistory.route) },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Screen.AddToQueue.route) {
            val queueViewModel: QueueViewModel = hiltViewModel()
            AddToQueueScreen(
                sessionId = "default_session", // In a real app, this would come from a shared state
                viewModel = queueViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.DjShift.route) {
            val djViewModel: DjViewModel = hiltViewModel()
            DjShiftScreen(
                viewModel = djViewModel,
                onNavigateToDashboard = { navController.navigate(Screen.DjDashboard.route) },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Login.route) { popUpTo(0) }
                }
            )
        }

        composable(Screen.DjDashboard.route) {
            val queueViewModel: QueueViewModel = hiltViewModel()
            DjDashboardScreen(
                sessionId = "default_session",
                viewModel = queueViewModel,
                onNavigateToSongHistory = { navController.navigate(Screen.SongHistory.route) },
                onSignOff = { navController.popBackStack() },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Login.route) { popUpTo(0) }
                }
            )
        }
    }
}
