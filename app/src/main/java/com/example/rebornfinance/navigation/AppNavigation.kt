package com.example.rebornfinance.navigation

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
import com.example.rebornfinance.feature.settings.SettingsScreen
import com.example.rebornfinance.feature.settings.SettingsViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel()
            HomeScreen(
                viewModel = viewModel,
                onNavigateToMovements = { navController.navigate(Screen.Movements.route) },
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
            val viewModel = MovementFormViewModel(context.applicationContext as android.app.Application, movementId)
            MovementFormScreen(
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
