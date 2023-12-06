package com.maguro.recipes.presentation.base

import android.graphics.drawable.VectorDrawable
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.maguro.recipes.presentation.list.ListScreen

private var scaffoldConfig by mutableStateOf(ScaffoldConfig())

data class ScaffoldConfig(
    var topBar: TopBarConfig? = null
)

@Composable
fun UpdateScaffold(tag: Any, block: ScaffoldConfig.() -> Unit) {
    LaunchedEffect(key1 = tag) {
        scaffoldConfig = ScaffoldConfig().apply(block)
    }
}

@Composable
fun RecipeScaffold() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { RecipeTopAppBar(topBarConfig = scaffoldConfig.topBar) },
        content = { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues)
            ) {
                ListScreen(
                    onRecipeClick = {}
                )
            }
        }
    )
}

