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

    // Phase 2: Projects
    object Projects : Screen("projects")
    object ProjectDetail : Screen("project_detail?projectId={projectId}") {
        fun createRoute(projectId: Long): String {
            return "project_detail?projectId=$projectId"
        }
    }
    object ProjectForm : Screen("project_form?projectId={projectId}") {
        fun createRoute(projectId: Long? = null): String {
            return if (projectId != null) "project_form?projectId=$projectId" else "project_form"
        }
    }
}
