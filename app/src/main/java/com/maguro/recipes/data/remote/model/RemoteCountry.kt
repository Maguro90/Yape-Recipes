package com.maguro.recipes.data.remote.model

import com.maguro.recipes.data.model.Coordinates
import com.maguro.recipes.data.model.Country

data class RemoteCountry (
    override val name: String,
    override val code: String,
    override val location: Coordinates,
    override val boundingBox: RemoteBoundingBox
): Country