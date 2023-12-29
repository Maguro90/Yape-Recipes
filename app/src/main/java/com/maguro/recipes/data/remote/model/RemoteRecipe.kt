package com.maguro.recipes.data.remote.model

import com.maguro.recipes.data.model.Recipe

data class RemoteRecipe(
    override val id: String,
    override val name: String,
    override val imageUrl: String,
    override val country: RemoteCountry,
    override val ingredients: List<String>,
    override val instructions: List<String>
): Recipe
