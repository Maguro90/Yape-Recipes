package com.maguro.recipes.presentation.screens.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.maguro.recipes.data.model.Coordinates
import com.maguro.recipes.data.model.Country
import com.maguro.recipes.data.model.Origin
import com.maguro.recipes.data.model.RecipeDetails
import com.maguro.recipes.presentation.base.TopBarConfig
import com.maguro.recipes.presentation.base.TopBarIconButton
import com.maguro.recipes.presentation.base.TopBarTitle
import com.maguro.recipes.presentation.base.TopBarType
import com.maguro.recipes.presentation.base.UpdateScaffold
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
    onBackClick: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val recipe = RecipeDetails.sample

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
        MapView(origin = recipe.origin)
    }
}

@Composable
private fun MapView(origin: Origin) {
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
                    zoomToRegion(origin)
                }
            }
        }
    )
}

private fun MapView.zoomToRegion(origin: Origin) {
    setRegionMarker(origin.fullRegionName, origin.coordinates)
    if (origin.isRegionOutsideOfMainland) {
        controller.apply {
            animateTo(origin.coordinates.geoPoint, 7.0, 3L)
        }
    } else {
        zoomToBoundingBox(
            origin.country.boundingBox, true,100
        )
    }
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

private val Country.boundingBox: BoundingBox
    get() = BoundingBox(
        northWest.latitude,
        southEast.longitude,
        southEast.latitude,
        northWest.longitude
    )

private val Origin.fullRegionName: String
    get() {
        val regionPart = if (region != null) {
            "${region.name}, "
        } else {
            ""
        }
        return "$regionPart ${country.countryName}"
    }

private val Country.countryName: String
    get() = Locale("", code).getDisplayCountry(Locale.getDefault())

private val Origin.coordinates: Coordinates
    get() = region?.coordinates ?: country.coordinates

private val Origin.isRegionOutsideOfMainland: Boolean
    get() {
        if (region == null)
            return false

        return listOf(
            region.coordinates.latitude < country.northWest.latitude,
            region.coordinates.longitude < country.northWest.longitude,
            (region.coordinates.latitude > country.southEast.latitude),
            (region.coordinates.longitude > country.southEast.longitude),
        ).any { it }
    }