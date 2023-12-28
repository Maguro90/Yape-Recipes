package com.maguro.recipes.presentation.screens.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.maguro.recipes.R
import com.maguro.recipes.data.model.Recipe
import com.maguro.recipes.data.repository.RequestResult
import com.maguro.recipes.presentation.base.TopBarActions
import com.maguro.recipes.presentation.base.TopBarConfig
import com.maguro.recipes.presentation.base.TopBarIconButton
import com.maguro.recipes.presentation.base.TopBarTitle
import com.maguro.recipes.presentation.base.TopBarType
import com.maguro.recipes.presentation.base.UpdateScaffold
import com.maguro.recipes.presentation.screens.utils.PullToRefreshBox


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    onLocationClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: DetailsViewModel = hiltViewModel()
) {

    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        state = pullToRefreshState,
        onRefresh = { viewModel.reload() }
    ) {
        val state = viewModel.recipe.collectAsStateWithLifecycle()

        when (val result = state.value) {
            is RequestResult.FirstLoad -> {
                Loading(onBackClick)
            }
            is RequestResult.Refresh -> {
                //Show nothing
            }
            is RequestResult.Success -> {
                pullToRefreshState.endRefresh()

                when (val recipe = result.data) {
                    null -> {}
                    else -> {
                        DetailsContent(
                            recipe = recipe,
                            onBackClick = onBackClick,
                            onLocationClick = onLocationClick
                        )
                    }
                }
            }
            is RequestResult.Error -> {}
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Loading(
    onBackClick: () -> Unit
) {
    UpdateScaffold(tag = "DetailsScreenLoading") {
        topBar = TopBarConfig(
            title = TopBarTitle.Empty,
            navIconButton = TopBarIconButton.Vector(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = onBackClick
            ),
        )
    }
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DetailsContent(
    recipe: Recipe,
    onBackClick: () -> Unit,
    onLocationClick: (String) -> Unit
) {
    UpdateScaffold(tag = "DetailsScreenLoaded") {
        topBar = TopBarConfig(
            title = TopBarTitle.Text(recipe.name),
            type = TopBarType.Large,
            scrollBehavior = { TopAppBarDefaults.exitUntilCollapsedScrollBehavior() },
            navIconButton = TopBarIconButton.Vector(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = onBackClick
            ),
            actions = TopBarActions.IconButtons(
                TopBarIconButton.Vector(
                    icon = Icons.Filled.LocationOn,
                    onClick = {
                        onLocationClick(recipe.id)
                    }
                )
            ),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(state = rememberScrollState())
            .padding(vertical = 24.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                //.alpha(0.2f)
                .clip(RoundedCornerShape(8.dp)),
            model = recipe.imageUrl,
            contentScale = ContentScale.FillWidth,
            contentDescription = recipe.name,
            placeholder = painterResource(id = R.drawable.image_placeholder)
        )

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider( modifier = Modifier.fillMaxWidth() )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(
                id = R.string.detailsScreen_ingredients
            ),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        for (ingredient in recipe.ingredients) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 16.dp),
            ) {
                Text(text = "- ")
                Text(
                    text = ingredient
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(
                id = R.string.detailsScreen_cookingSteps
            ),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        for ((index, step) in recipe.instructions.withIndex()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 16.dp),
            ) {
                Text(text = "${index + 1}. ")
                Text(
                    text = step
                )
            }
        }
    }
}