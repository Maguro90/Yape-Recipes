package com.maguro.recipes.presentation.screens.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maguro.recipes.data.model.Coordinates
import com.maguro.recipes.data.model.Country
import com.maguro.recipes.data.model.Recipe
import com.maguro.recipes.data.repository.ErrorType
import com.maguro.recipes.data.repository.RequestResult
import com.maguro.recipes.presentation.base.TopBarConfig
import com.maguro.recipes.presentation.base.TopBarIconButton
import com.maguro.recipes.presentation.base.TopBarTitle
import com.maguro.recipes.presentation.base.TopBarType
import com.maguro.recipes.presentation.base.UpdateScaffold
import com.maguro.recipes.presentation.screens.utils.EmptyContent
import com.maguro.recipes.presentation.screens.utils.ErrorContent
import com.maguro.recipes.presentation.screens.utils.ErrorSnackbar
import com.maguro.recipes.presentation.screens.utils.PullToRefreshBox
import kotlinx.coroutines.CoroutineScope
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
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
            is RequestResult.WithData -> {
                val recipeState = remember {
                    derivedStateOf {
                        (state.value as RequestResult.WithData).data
                    }
                }

                val recipe = recipeState.value

                if (result is RequestResult.WithData.Loaded) {
                    pullToRefreshState.endRefresh()
                }

                when {
                    recipe != null -> {
                        MapContent(
                            recipe = recipe,
                            onBackClick = onBackClick
                        )
                        ErrorSnackbar(
                            coroutineScope = coroutineScope,
                            snackbarHostState = snackbarHostState,
                            error = result.consumeError())
                    }
                    result is RequestResult.WithData.Refresh -> {
                        // Show nothing
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
@OptIn(ExperimentalMaterial3Api::class)
private fun Loading(
    onBackClick: () -> Unit
) {
    UpdateScaffold(tag = "MapScreen") {
        topBar = TopBarConfig(
            title = TopBarTitle.Empty,
            navIconButton = TopBarIconButton.Vector(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = onBackClick
            )
        )
    }
    LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MapContent(
    recipe: Recipe,
    onBackClick: () -> Unit
) {
    UpdateScaffold(tag = "MapScreen") {
        topBar = TopBarConfig(
            title = TopBarTitle.Text(recipe.name),
            type = TopBarType.Large,
            scrollBehavior = { TopAppBarDefaults.exitUntilCollapsedScrollBehavior() },
            navIconButton = TopBarIconButton.Vector(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = onBackClick
            )
        )
    }

    Box(
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
    ) {
        MapView(country = recipe.country)
    }
}

@Composable
private fun MapView(country: Country) {
    AndroidView(
        factory = { context ->
            Configuration.getInstance().apply {
                userAgentValue = "recipe-app"
            }
            MapView(context).apply {
                clipToOutline = true
                isHorizontalMapRepetitionEnabled = false
                isVerticalMapRepetitionEnabled = false
                setTileSource(TileSourceFactory.MAPNIK)
                zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                addOnFirstLayoutListener { _, _, _, _, _ ->
                    zoomToCountry(country)
                }
            }
        }
    )
}

private fun MapView.zoomToCountry(country: Country) {
    setRegionMarker(country.localizedName, country.location)
    zoomToBoundingBox(
        country.implBoundingBox, true,100
    )
}

private fun MapView.setRegionMarker(fullRegionName: String, coordinates: Coordinates) {
    Marker(this).apply {
        position = coordinates.geoPoint
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        title = fullRegionName
        showInfoWindow()
    }.also { marker ->
        overlays.add(marker)
    }
}

private val Coordinates.geoPoint: GeoPoint
    get() = GeoPoint(latitude, longitude)

private val Country.implBoundingBox: BoundingBox
    get() = BoundingBox(
        boundingBox.northWest.latitude,
        boundingBox.southEast.longitude,
        boundingBox.southEast.latitude,
        boundingBox.northWest.longitude
    )

private val Country.localizedName: String
    get() = Locale("", code).getDisplayCountry(Locale.getDefault())
