package com.maguro.recipes.data.model

data class Country (
    val code: String,
    val coordinates: Coordinates,
    val northWest: Coordinates,
    val southEast: Coordinates,
)