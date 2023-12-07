package com.maguro.recipes.presentation.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.maguro.recipes.presentation.navigation.RecipeNavigation

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScaffold() {

    val scrollBehavior = scaffoldConfig.topBar?.scrollBehavior?.invoke()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .run {
               if (scrollBehavior != null) {
                   nestedScroll(scrollBehavior.nestedScrollConnection)
               } else {
                   this
               }
            }
        ,
        topBar = {
            RecipeTopAppBar(
                topBarConfig = scaffoldConfig.topBar,
                scrollBehavior = scrollBehavior
            )
         },
        content = { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues)
            ) {
                RecipeNavigation()
            }
        }
    )
}

