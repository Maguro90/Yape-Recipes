package com.maguro.recipes.data.remote.model

import com.maguro.recipes.data.model.BoundingBox
import com.maguro.recipes.data.model.Coordinates

data class RemoteBoundingBox (
    override val northWest: Coordinates,
    override val southEast: Coordinates
): BoundingBox