package com.maguro.recipes.data.local.model

import androidx.room.Embedded
import com.maguro.recipes.data.model.BoundingBox
import com.maguro.recipes.data.model.Coordinates

data class LocalBoundingBox(
    @Embedded("northWest_")
    override val northWest: Coordinates,
    @Embedded("southEast_")
    override val southEast: Coordinates
): BoundingBox {

    constructor(
        other: BoundingBox
    ): this(
        northWest = other.northWest,
        southEast = other.southEast
    )

}

