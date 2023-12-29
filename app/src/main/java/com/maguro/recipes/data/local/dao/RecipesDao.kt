package com.maguro.recipes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.maguro.recipes.data.local.model.LocalCountry
import com.maguro.recipes.data.local.model.LocalIngredient
import com.maguro.recipes.data.local.model.LocalInstruction
import com.maguro.recipes.data.local.model.LocalRecipe
import com.maguro.recipes.data.local.model.LocalRecipeDetails
import com.maguro.recipes.data.model.Recipe

@Dao
abstract class RecipesDao {

    @Transaction
    @Query("SELECT * FROM LocalRecipeDetails")
    abstract suspend fun getAll(): List<LocalRecipe>

    @Transaction
    @Query("SELECT * FROM LocalRecipeDetails WHERE id = :id")
    abstract suspend fun getById(id: String): LocalRecipe?

    @Transaction
    open suspend fun insertRecipe(recipe: Recipe) {
        insertCountry(LocalCountry(recipe.country))
        insertRecipeDetails(LocalRecipeDetails(recipe))
        insertIngredients(LocalIngredient.map(recipe.id, recipe.ingredients))
        insertInstructions(LocalInstruction.map(recipe.id, recipe.instructions))
    }

    @Transaction
    open suspend fun insertRecipes(recipes: List<Recipe>) {
        for (recipe in recipes) {
            insertRecipe(recipe)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertRecipeDetails(recipe: LocalRecipeDetails)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract suspend fun insertCountry(country: LocalCountry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertInstruction(instruction: LocalInstruction)

    @Query("SELECT COUNT(*) FROM LocalInstruction")
    protected abstract suspend fun countInstructions(): Int

    @Transaction
    protected open suspend fun insertInstructions(instructions: List<LocalInstruction>) {
        for (instruction in instructions) {
            insertInstruction(instruction)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertIngredient(ingredient: LocalIngredient)

    @Transaction
    protected open suspend fun insertIngredients(ingredients: List<LocalIngredient>) {
        for (ingredient in ingredients) {
            insertIngredient(ingredient)
        }
    }
}