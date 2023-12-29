package com.maguro.recipes.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = LocalRecipeDetails::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = CASCADE,
            deferred = true
        )
    ],
    indices = [
        Index("recipeId")
    ]
)
data class LocalIngredient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val recipeId: String,
    val text: String
) {

    companion object {
        fun map(recipeId: String, ingredients: List<String>): List<LocalIngredient> =
            ingredients.map {
                LocalIngredient(
                    recipeId = recipeId,
                    text = it
                )
            }
    }
}
