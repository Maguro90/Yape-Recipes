package com.maguro.recipes.domain

import com.maguro.recipes.data.model.Recipe
import com.maguro.recipes.data.repository.RecipeRepository
import com.maguro.recipes.data.repository.RequestResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetRecipeByIdUseCase {
    operator fun invoke(id: String): Flow<RequestResult<Recipe?>>
}

class GetRecipeByIdUseCaseImpl @Inject constructor(
    private val recipeRepository: RecipeRepository
): GetRecipeByIdUseCase {
    override fun invoke(id: String): Flow<RequestResult<Recipe?>> =
        recipeRepository.getById(id)
}