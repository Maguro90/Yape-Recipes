package com.maguro.recipes.domain

import com.maguro.recipes.data.model.Recipe
import com.maguro.recipes.data.repository.RecipeRepository
import com.maguro.recipes.data.repository.RequestResult
import com.maguro.recipes.data.repository.mapResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetAllRecipesSortedByNameUseCase {
    operator fun invoke(): Flow<RequestResult<List<Recipe>>>
}

class GetAllRecipesSortedByNameUseCaseImpl @Inject constructor(
    private val recipeRepository: RecipeRepository
) : GetAllRecipesSortedByNameUseCase{

    override fun invoke(): Flow<RequestResult<List<Recipe>>> =
        recipeRepository.all
            .mapResult { list ->
                list.sortedBy { it.name }
            }

}