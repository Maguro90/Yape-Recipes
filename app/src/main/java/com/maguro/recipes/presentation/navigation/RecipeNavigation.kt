package com.maguro.recipes.presentation.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.maguro.recipes.presentation.screens.details.DetailsScreen
import com.maguro.recipes.presentation.screens.list.ListScreen
import com.maguro.recipes.presentation.screens.map.MapScreen

@Composable
fun RecipeNavigation() {
    val navController = rememberNavController()
    
    RecipeNavHost(navController = navController, startDestination = Route.ListScreen) {
        screen(route = Route.ListScreen) {
            ListScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(Route.DetailsScreen.withId(recipeId))
                }
            )
        }
        screen(route = Route.DetailsScreen) {
            DetailsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLocationClick = { recipeId ->
                    navController.navigate(Route.MapScreen.withId(recipeId))
                }
            )
        }
        screen(route = Route.MapScreen) {
            MapScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
private fun RecipeNavHost(
    navController: NavHostController,
    startDestination: Route,
    builder: NavGraphBuilder.() -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.path,
        builder = builder
    )
}

private fun NavGraphBuilder.screen(
    route: Route,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route.path,
        arguments = route.arguments,
        content = content
    )
}