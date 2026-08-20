package com.eman.micq.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object RoleSelection : Screen("role_selection")
    object Register : Screen("register")
    object Login : Screen("login")
    object AdminDashboard : Screen("admin_dashboard")
    object DjShift : Screen("dj_shift")
    object DjDashboard : Screen("dj_dashboard")
    object PerformerDashboard : Screen("performer_dashboard")
    object AddToQueue : Screen("add_to_queue")
    object ShiftHistory : Screen("shift_history")
    object SongHistory : Screen("song_history")
}
