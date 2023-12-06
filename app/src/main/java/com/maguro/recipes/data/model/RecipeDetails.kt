package com.maguro.recipes.data.model

data class RecipeDetails(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val description: String,
    val ingredients: List<String>,
    val cookingSteps: List<String>
) {
    companion object {
        val sample = RecipeDetails(
            id = 1,
            name = "Aguachile de camarón",
            imageUrl = "https://cdn7.kiwilimon.com/recetaimagen/28638/28924.jpg",
            description = "Estos días de calor prepara un delicioso, fresco y picosito aguachile de camarón que va acompañado con tostadas de maíz y decorado con cebollita morada, y chile serrano. Además de su intenso sabor lleva una combinación de salsa inglesa y jugo de limón. ¡Te encantará!",
            ingredients = listOf(
                "1 kilo de camarón mediano, sin cáscara, limpios y en corte mariposa",
                "1 taza de jugo de limón colado",
                "2 cucharadas de caldo de pollo",
                "1 cebolla morada, fileteada y desflemada",
                "2 cucharadas de salsa inglesa",
                "2 chiles serranos, sin tallo y sin semillas",
                "1 pepino, pelado, sin semillas y cortado en medias lunas",
                "8 tostadas de maíz horneadas"
            ),
            cookingSteps = listOf(
                "Mezclar en un tazón los camarones con el jugo de limón, el caldo de pollo y la cebolla",
                "Refrigerar por 20 minutos o hasta que los camarones cambien de color",
                "Colocar los camarones y cebolla en un tazon. Reservar el jugo de la marinada",
                "Licuar el jugo de la marinada con la salsa inglesa y los chiles serranos",
                "Verter esta mezcla al platón con los camarones y añadir también los pepinos. Refrigerar nuevamente por 15 minutos más",
                "Servir y acompañar con las tostadas de maíz"
            )
        )
    }
}
