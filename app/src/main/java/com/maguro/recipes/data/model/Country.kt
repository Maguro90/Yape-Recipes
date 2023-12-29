package com.maguro.recipes.data.model

interface Country {
    val name: String
    val code: String
    val location: Coordinates
    val boundingBox: BoundingBox
}