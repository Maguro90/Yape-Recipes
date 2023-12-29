package com.maguro.recipes.data.local.model

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.maguro.recipes.data.model.Recipe

data class LocalRecipe (
    @Embedded val details: LocalRecipeDetails,
    @Relation(
        parentColumn = "countryCode",
        entityColumn = "code"
    )
    override val country: LocalCountry,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId",
        entity = LocalIngredient::class,
        projection = ["text"]
    )
    override val ingredients: List<String>,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId",
        entity = LocalInstruction::class,
        projection = ["text"]
    )
    override val instructions: List<String>
): Recipe {

    @field:Ignore
    override val id: String = details.id
    @field:Ignore
    override val name: String = details.name
    @field:Ignore
    override val imageUrl: String = details.imageUrl

}