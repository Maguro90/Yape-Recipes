package com.maguro.recipes.data.model

interface BoundingBox {
    val northWest: Coordinates
    val southEast: Coordinates
}