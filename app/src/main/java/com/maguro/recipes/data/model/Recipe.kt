package com.maguro.recipes.data.model

interface Recipe {
    val id: String
    val name: String
    val imageUrl: String
    val country: Country
    val ingredients: List<String>
    val instructions: List<String>
}
