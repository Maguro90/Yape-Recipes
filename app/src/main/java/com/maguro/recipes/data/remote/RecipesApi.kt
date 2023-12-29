package com.maguro.recipes.data.remote

import com.maguro.recipes.data.remote.model.RemoteRecipe
import retrofit2.http.GET
import retrofit2.http.Path

interface RecipesApi {

    @GET("recipes")
    suspend fun fetchAll(): List<RemoteRecipe>

    @GET("recipes/{id}")
    suspend fun fetchById(
        @Path("id") id: String
    ): RemoteRecipe?

}