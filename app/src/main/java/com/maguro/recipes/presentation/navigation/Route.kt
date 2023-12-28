package com.maguro.recipes.presentation.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

abstract class Route {
    abstract val domain: String
    abstract val arguments: List<NamedNavArgument>

    val path: String by lazy {
        "$domain/" + arguments.joinToString(separator = "/") { arg ->
            "{${arg.name}}"
        }
    }
    object ListScreen : Route() {
        override val domain: String = "list"
        override val arguments: List<NamedNavArgument> = emptyList()
    }

    object DetailsScreen : RouteWithId(domain = "details")

    object MapScreen : RouteWithId(domain = "map")

}

open class RouteWithId protected constructor(
    override val domain: String,
    additionalArgs: List<NamedNavArgument> = emptyList()
) : Route() {

    private val idArg =
        navArgument(Args.ID.value) {
            type = NavType.StringType
            nullable = false
        }

    override val arguments: List<NamedNavArgument> =
        additionalArgs + idArg

    fun withId(id: String): String {
        return path
            .replace("{${Args.ID.value}}", id)
    }

    private enum class Args(val value: String) {
        ID(RouteWithId.ID),
    }

    companion object {
        const val ID = "id"
    }
}
