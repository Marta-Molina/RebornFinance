package com.example.rebornfinance.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rebornfinance.feature.home.HomeScreen
import com.example.rebornfinance.feature.home.HomeViewModel
import com.example.rebornfinance.feature.movements.MovementFormScreen
import com.example.rebornfinance.feature.movements.MovementFormViewModel
import com.example.rebornfinance.feature.movements.MovementsScreen
import com.example.rebornfinance.feature.movements.MovementsViewModel
import com.example.rebornfinance.feature.projects.ProjectDetailScreen
import com.example.rebornfinance.feature.projects.ProjectDetailViewModel
import com.example.rebornfinance.feature.projects.ProjectFormScreen
import com.example.rebornfinance.feature.projects.ProjectFormViewModel
import com.example.rebornfinance.feature.projects.ProjectsScreen
import com.example.rebornfinance.feature.projects.ProjectsViewModel
import com.example.rebornfinance.feature.settings.SettingsScreen
import com.example.rebornfinance.feature.settings.SettingsViewModel
import com.example.rebornfinance.feature.statistics.StatisticsScreen
import com.example.rebornfinance.feature.statistics.StatisticsViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val app = context.applicationContext as Application

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel()
            HomeScreen(
                viewModel = viewModel,
                onNavigateToMovements = { navController.navigate(Screen.Movements.route) },
                onNavigateToProjects = { navController.navigate(Screen.Projects.route) },
                onNavigateToStatistics = { navController.navigate(Screen.Statistics.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToAddMovement = { navController.navigate(Screen.MovementForm.createRoute()) },
                onNavigateToEditMovement = { id -> navController.navigate(Screen.MovementForm.createRoute(id)) }
            )
        }

        composable(Screen.Movements.route) {
            val viewModel: MovementsViewModel = viewModel()
            MovementsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAdd = { navController.navigate(Screen.MovementForm.createRoute()) },
                onNavigateToEdit = { id -> navController.navigate(Screen.MovementForm.createRoute(id)) }
            )
        }

        composable(
            route = "movement_form?movementId={movementId}",
            arguments = listOf(navArgument("movementId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val movementIdArg = backStackEntry.arguments?.getLong("movementId")
            val movementId = if (movementIdArg != null && movementIdArg > 0L) movementIdArg else null
            val viewModel = MovementFormViewModel(app, movementId)
            MovementFormScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Projects.route) {
            val viewModel: ProjectsViewModel = viewModel()
            ProjectsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddProject = { navController.navigate(Screen.ProjectForm.createRoute()) },
                onNavigateToDetail = { id -> navController.navigate(Screen.ProjectDetail.createRoute(id)) }
            )
        }

        composable(
            route = "project_detail?projectId={projectId}",
            arguments = listOf(navArgument("projectId") {
                type = NavType.LongType
            })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0L
            val viewModel = ProjectDetailViewModel(app, projectId)
            ProjectDetailScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate(Screen.ProjectForm.createRoute(id)) }
            )
        }

        composable(
            route = "project_form?projectId={projectId}",
            arguments = listOf(navArgument("projectId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val projectIdArg = backStackEntry.arguments?.getLong("projectId")
            val projectId = if (projectIdArg != null && projectIdArg > 0L) projectIdArg else null
            val viewModel = ProjectFormViewModel(app, projectId)
            ProjectFormScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Statistics.route) {
            val viewModel: StatisticsViewModel = viewModel()
            StatisticsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = viewModel()
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
