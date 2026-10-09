package com.example.rebornfinance.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Movements : Screen("movements")
    object Settings : Screen("settings")
    object MovementForm : Screen("movement_form?movementId={movementId}") {
        fun createRoute(movementId: Long? = null): String {
            return if (movementId != null) "movement_form?movementId=$movementId" else "movement_form"
        }
    }
}
