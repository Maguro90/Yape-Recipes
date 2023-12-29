package com.maguro.recipes.data.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maguro.recipes.data.model.Coordinates
import com.maguro.recipes.data.model.Country

@Entity
data class LocalCountry(
    @PrimaryKey
    override val code: String,
    override val name: String,
    @Embedded
    override val location: Coordinates,
    @Embedded("bounds_")
    override val boundingBox: LocalBoundingBox
): Country {

    constructor(
        other: Country
    ): this(
        code = other.code,
        name = other.name,
        location = other.location,
        boundingBox = LocalBoundingBox(other.boundingBox)
    )

}