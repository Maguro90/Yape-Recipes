package com.maguro.recipes.presentation.screens.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maguro.recipes.R
import com.maguro.recipes.data.model.Recipe
import com.maguro.recipes.data.repository.ErrorType
import com.maguro.recipes.data.repository.RequestResult
import com.maguro.recipes.presentation.base.TopBarConfig
import com.maguro.recipes.presentation.base.TopBarTitle
import com.maguro.recipes.presentation.base.UpdateScaffold
import com.maguro.recipes.presentation.screens.utils.EmptyContent
import com.maguro.recipes.presentation.screens.utils.ErrorContent
import com.maguro.recipes.presentation.screens.utils.ErrorSnackbar
import com.maguro.recipes.presentation.screens.utils.PullToRefreshBox
import kotlinx.coroutines.CoroutineScope


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
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
            is RequestResult.WithData -> {
                val recipes by remember {
                    derivedStateOf {
                        (state.value as RequestResult.WithData).data
                    }
                }

                if (result is RequestResult.WithData.Loaded) {
                    pullToRefreshState.endRefresh()
                }

                when {
                    recipes.isNotEmpty() -> {
                        RecipeList(
                            recipes = recipes,
                            onRecipeClick = onRecipeClick
                        )
                        ErrorSnackbar(
                            coroutineScope = coroutineScope,
                            snackbarHostState = snackbarHostState,
                            error = result.consumeError())
                    }
                    result is RequestResult.WithData.Refresh -> {
                        //Show nothing
                    }
                    result.consumeError() is ErrorType.None -> {
                        EmptyContent (
                            modifier = Modifier.fillMaxSize(),
                            onRetryClick = {
                                pullToRefreshState.startRefresh()
                            }
                        )
                    }
                    else -> {
                        ErrorContent(
                            modifier = Modifier.fillMaxSize(),
                            errorType = result.error,
                            onRetryClick = {
                                pullToRefreshState.startRefresh()
                            }
                        )
                    }
                }
            }
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