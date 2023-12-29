package com.maguro.recipes.data.repository

import com.maguro.recipes.data.local.dao.RecipesDao
import com.maguro.recipes.data.local.model.LocalCountry
import com.maguro.recipes.data.local.model.LocalIngredient
import com.maguro.recipes.data.local.model.LocalInstruction
import com.maguro.recipes.data.local.model.LocalRecipe
import com.maguro.recipes.data.local.model.LocalRecipeDetails
import com.maguro.recipes.data.model.Recipe

class FakeRecipesDao : RecipesDao() {

    val stored = mutableListOf<LocalRecipe>()

    override suspend fun getAll(): List<LocalRecipe> = stored

    override suspend fun getById(id: String): LocalRecipe? = stored.firstOrNull {
        it.id == id
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        val newRecipe = LocalRecipe(
            details = LocalRecipeDetails(recipe),
            country = LocalCountry(recipe.country),
            instructions = recipe.instructions,
            ingredients = recipe.ingredients
        )

        stored.indexOfFirst { it.id == newRecipe.id }
            .takeIf { it > -1 }
            ?.also {
                stored[it] = newRecipe
            } ?: stored.add(newRecipe)
    }

    override suspend fun insertRecipeDetails(recipe: LocalRecipeDetails) {
        TODO("Not yet implemented")
    }

    override suspend fun insertCountry(country: LocalCountry) {
        TODO("Not yet implemented")
    }

    override suspend fun insertInstruction(instruction: LocalInstruction) {
        TODO("Not yet implemented")
    }

    override suspend fun countInstructions(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun insertIngredient(ingredient: LocalIngredient) {
        TODO("Not yet implemented")
    }
}