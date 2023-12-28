package com.maguro.recipes.presentation.base

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
data class TopBarConfig (
    var title: TopBarTitle? = null,
    var type: TopBarType = TopBarType.Small,
    var navIconButton: TopBarIconButton? = null,
    var actions: TopBarActions? = null,
    var scrollBehavior: (@Composable () -> TopAppBarScrollBehavior)? = null
)

enum class TopBarType {
    Small,
    Large
}

sealed interface TopBarTitle {
    object Empty: TopBarTitle
    class Text(val title: String): TopBarTitle
    class StringResource(@StringRes val id: Int): TopBarTitle
    class Custom(val block: @Composable () -> Unit) : TopBarTitle
}

sealed interface TopBarIconButton {
    class Vector(
        val icon: ImageVector,
        val description: String? = null,
        val onClick: () -> Unit
    ): TopBarIconButton
    class Resource(
        @DrawableRes val icon: Int,
        val description: String? = null,
        val onClick: () -> Unit
    ): TopBarIconButton
}

sealed interface TopBarActions {
    class IconButtons(vararg val iconButtons: TopBarIconButton): TopBarActions
    class Custom(val block: @Composable RowScope.() -> Unit): TopBarActions
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeTopAppBar(
    topBarConfig: TopBarConfig?,
    scrollBehavior: TopAppBarScrollBehavior?
) {
    if (topBarConfig != null) {
        when (topBarConfig.type) {
            TopBarType.Small ->
                TopAppBar(
                    title = { TopBarTitle(topBarConfig.title) },
                    navigationIcon = { TopBarIconButton(topBarConfig.navIconButton) },
                    actions = { TopBarActions(topBarConfig.actions) },
                    scrollBehavior = scrollBehavior
                )
            TopBarType.Large ->
                LargeTopAppBar(
                    title = { TopBarTitle(topBarConfig.title) },
                    navigationIcon = { TopBarIconButton(topBarConfig.navIconButton) },
                    actions = { TopBarActions(topBarConfig.actions)},
                    scrollBehavior = scrollBehavior
                )
        }
    }
}

@Composable
private fun TopBarTitle(title: TopBarTitle?) {
    when (title) {
        is TopBarTitle.Empty -> {}
        is TopBarTitle.Text -> Text(title.title)
        is TopBarTitle.StringResource -> Text(stringResource(title.id))
        is TopBarTitle.Custom -> title.block()
        null -> {}
    }
}

@Composable
private fun TopBarIconButton(navIconButton: TopBarIconButton?) {
    when (navIconButton) {
        is TopBarIconButton.Vector -> {
            IconButton(onClick = navIconButton.onClick) {
                Icon(
                    imageVector = navIconButton.icon,
                    contentDescription = navIconButton.description
                )
            }
        }
        is TopBarIconButton.Resource -> {
            IconButton(onClick = navIconButton.onClick) {
                Icon(
                    painter = painterResource(id = navIconButton.icon),
                    contentDescription = navIconButton.description
                )
            }
        }
        null -> {}
    }
}

@Composable
private fun RowScope.TopBarActions(topBarActions: TopBarActions?) {
    when (topBarActions) {
        is TopBarActions.IconButtons -> {
            for(iconButton in topBarActions.iconButtons) {
                TopBarIconButton(navIconButton = iconButton)
            }
        }
        is TopBarActions.Custom -> {
            topBarActions.block(this)
        }
        null -> {}
    }
}
