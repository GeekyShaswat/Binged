package com.movie.binged.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import coil.util.Logger
import com.movie.binged.screens.DetailScreen
import com.movie.binged.screens.Homepage
import com.movie.binged.screens.SplashScreen
import com.movie.binged.screens.VideoPlayer
import com.movie.binged.utils.Utils

sealed class Screens( val route : String){

    object Home : Screens("home")
    object Detail : Screens("details/{id}/{type}"){
        fun createRoute( id : String, type : String): String{
            return "details/$id/$type"
        }
    }
    object VideoPlayer : Screens("player/{id}/{type}/{season}/{episode}"){
        fun createRoute(
            id: String,
            type: String,
            season: Int?,
            episode: Int?
        ): String {
            return "player/$id/$type/$season/$episode"
        }
    }

    object Splash : Screens("splash")
}

@Composable
fun AppNavigation( navController: NavHostController) {

    NavHost(
        navController,
        startDestination = Screens.Splash.route
    ) {
        composable(
            route = Screens.Splash.route
        ){
            SplashScreen(navController)
        }

        composable(
            route = Screens.Home.route
        ){
            Homepage( onCardClick = { id, type ->
                navController.navigate(Screens.Detail.createRoute(id,type))
            }
            )
        }
        composable(
            route = Screens.Detail.route,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType }
            )
        ){ backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            val type = backStackEntry.arguments?.getString("type")

            DetailScreen(id, type, navController)

        }
        composable(
            route = "player/{id}/{type}/{season}/{episode}",
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
        ){ backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            val type = backStackEntry.arguments?.getString("type")
            val season = backStackEntry.arguments?.getInt("season")
            val episode = backStackEntry.arguments?.getInt("episode")

            when(backStackEntry.arguments?.getString("type")){
                "movie" -> {
                    VideoPlayer(Utils.urlForMovie(id), navController)
                }
                "show" -> {
                    Log.d("TAG"," show player called ")
                    VideoPlayer(Utils.urlForSeries(
                        id,
                        season,
                        episode
                        ), navController)
                }

            }
        }
    }
}