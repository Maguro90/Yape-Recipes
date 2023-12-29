package com.maguro.recipes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.maguro.recipes.data.local.dao.RecipesDao
import com.maguro.recipes.data.local.model.LocalCountry
import com.maguro.recipes.data.local.model.LocalIngredient
import com.maguro.recipes.data.local.model.LocalInstruction
import com.maguro.recipes.data.local.model.LocalRecipeDetails

@Database(
    entities = [
        LocalRecipeDetails::class,
        LocalCountry::class,
        LocalInstruction::class,
        LocalIngredient::class
    ],
    version = 1
)
abstract class RecipeDatabase: RoomDatabase() {
    abstract fun recipeDao(): RecipesDao
}