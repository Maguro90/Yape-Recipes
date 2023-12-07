package com.maguro.recipes.presentation.screens.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.maguro.recipes.R
import com.maguro.recipes.data.model.Recipe
import com.maguro.recipes.presentation.base.TopBarConfig
import com.maguro.recipes.presentation.base.TopBarTitle
import com.maguro.recipes.presentation.base.UpdateScaffold


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    onRecipeClick: (recipeId: Long) -> Unit,
    viewModel: ListViewModel = hiltViewModel()
) {

    UpdateScaffold(tag = "ListScreen") {
        topBar = TopBarConfig(
            title = TopBarTitle.StringResource(R.string.listScreen_title)
        )
    }

    LazyColumn {
        items(
            Recipe.sample,
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