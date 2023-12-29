package com.maguro.recipes.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maguro.recipes.data.model.Recipe

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = LocalCountry::class,
            parentColumns = ["code"],
            childColumns = ["countryCode"],
            onDelete = CASCADE,
            deferred = true
        )
    ],
    indices = [
        Index("countryCode")
    ]
)
data class LocalRecipeDetails(
    @PrimaryKey
    val id: String,
    val name: String,
    val imageUrl: String,
    val countryCode: String,
) {

    constructor(
        other: Recipe
    ): this(
        id = other.id,
        name = other.name,
        imageUrl = other.imageUrl,
        countryCode = other.country.code
    )

}
