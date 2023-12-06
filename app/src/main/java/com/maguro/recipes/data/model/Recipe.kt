package com.maguro.recipes.data.model

data class Recipe (
    val id: Long,
    val name: String,
    val imageUrl: String
) {

    companion object {
        val sample = listOf(
            Recipe(
                id = 1,
                name = "Aguachile de camarón",
                imageUrl = "https://cdn7.kiwilimon.com/recetaimagen/28638/28924.jpg"
            ),
            Recipe(
                id = 2,
                name = "Aguachile de camarón",
                imageUrl = "https://cdn7.kiwilimon.com/recetaimagen/28638/28924.jpg"
            ),
            Recipe(
                id = 3,
                name = "Aguachile de camarón",
                imageUrl = "https://cdn7.kiwilimon.com/recetaimagen/28638/28924.jpg"
            ),
        )
    }

}