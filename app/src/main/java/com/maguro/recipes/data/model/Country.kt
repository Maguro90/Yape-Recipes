package com.maguro.recipes.data.model

data class Country (
    val name: String,
    val code: String,
    val location: Coordinates,
    val boundingBox: BoundingBox
)