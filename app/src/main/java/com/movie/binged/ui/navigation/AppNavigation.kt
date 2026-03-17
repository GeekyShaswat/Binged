package com.movie.binged.ui.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.movie.binged.data.repository.UserRepository
import com.movie.binged.ui.screens.DetailScreen
import com.movie.binged.ui.screens.Homepage
import com.movie.binged.ui.screens.SplashScreen
import com.movie.binged.ui.screens.UserProfileScreen
import com.movie.binged.ui.screens.UserSetupScreen
import com.movie.binged.ui.screens.VideoPlayer
import com.movie.binged.utils.Utils
import com.movie.binged.viewmodel.UserViewModel
import com.movie.binged.viewmodel.UserViewModelFactory

sealed class Screens(val route: String) {
    object Home : Screens("home")
    object Detail : Screens("details/{id}/{type}") {
        fun createRoute(id: String, type: String) = "details/$id/$type"
    }
    object VideoPlayer : Screens("player/{id}/{type}/{season}/{episode}") {
        fun createRoute(id: String, type: String, season: Int?, episode: Int?) =
            "player/$id/$type/$season/$episode"
    }
    object Splash : Screens("splash")
    object UserSetup : Screens("setup")
    object UserProfile : Screens("profile")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    navController: NavHostController,
    userRepository: UserRepository
) {
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userRepository)
    )
    val profile by userViewModel.profileFlow.collectAsState(initial = null)
    if (profile == null) return
    val (name, genres) = profile!!
    val isFirstTime = name.isBlank()

    NavHost(navController, startDestination = Screens.Splash.route) {

        composable(Screens.Splash.route) {
            SplashScreen(
                navController = navController,
                nextDestination = if (isFirstTime) Screens.UserSetup.route
                else Screens.Home.route
            )
        }

        composable(Screens.UserSetup.route) {
            UserSetupScreen(
                onSave = { savedName, savedGenres ->
                    userViewModel.saveProfile(savedName, savedGenres)
                    navController.navigate(Screens.Home.route) {
                        popUpTo(Screens.UserSetup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screens.UserProfile.route) {
            UserProfileScreen(
                name = name,
                savedGenres = genres,
                onGenresSaved = { newGenres ->
                    userViewModel.saveGenres(newGenres)
                },
                navController,
                onLogout = {
                    userViewModel.logout {
                        navController.navigate(Screens.UserSetup.route) {
                            // Clear entire back stack so user can't go back
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screens.Home.route) {
            Homepage(
                onCardClick = { id, type ->
                    navController.navigate(Screens.Detail.createRoute(id, type))
                },
                navController = navController,
                userRepository = userRepository
            )
        }

        composable(
            route = Screens.Detail.route,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            val type = backStackEntry.arguments?.getString("type")
            DetailScreen(id, type, navController, userRepository)
        }

        composable(
            route = Screens.VideoPlayer.route,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType },
                navArgument("season") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("episode") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            val type = backStackEntry.arguments?.getString("type")
            val season = backStackEntry.arguments?.getInt("season")
            val episode = backStackEntry.arguments?.getInt("episode")

            when (type) {
                "movie" -> {
                    val url = Utils.urlForMovie(id)
                    Log.d("TAG", "url is: $url")
                    VideoPlayer(url, navController)
                }
                "show" -> {
                    Log.d("TAG", "show player called")
                    VideoPlayer(Utils.urlForSeries(id, season, episode), navController)
                }
            }
        }
    }
}