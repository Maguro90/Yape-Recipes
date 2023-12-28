package com.maguro.recipes.presentation.screens.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maguro.recipes.R
import com.maguro.recipes.data.model.Recipe
import com.maguro.recipes.data.repository.RequestResult
import com.maguro.recipes.presentation.base.TopBarConfig
import com.maguro.recipes.presentation.base.TopBarTitle
import com.maguro.recipes.presentation.base.UpdateScaffold
import com.maguro.recipes.presentation.screens.utils.PullToRefreshBox


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    onRecipeClick: (recipeId: String) -> Unit,
    viewModel: ListViewModel = hiltViewModel()
) {

    UpdateScaffold(tag = "ListScreen") {
        topBar = TopBarConfig(
            title = TopBarTitle.StringResource(R.string.listScreen_title)
        )
    }

    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        state = pullToRefreshState,
        onRefresh = { viewModel.reload() }
    ) {
        val state = viewModel.recipes.collectAsStateWithLifecycle()

        when (val result = state.value) {
            is RequestResult.FirstLoad -> {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
            is RequestResult.Refresh -> {
                //Show nothing
            }
            is RequestResult.Success -> {
                pullToRefreshState.endRefresh()
                RecipeList(
                    recipes = result.data,
                    onRecipeClick = onRecipeClick,
                )
            }
            is RequestResult.Error -> {}
        }
    }
}

@Composable
private fun RecipeList(
    recipes: List<Recipe>,
    onRecipeClick: (String) -> Unit,
) {
    LazyColumn {
        items(
            recipes,
            key = { recipe -> recipe.id }
        ) {recipe ->
            ListItem(
                recipe = recipe,
                onClick = {
                    onRecipeClick(it.id)
                }
            )
        }
    }
}