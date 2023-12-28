package com.maguro.recipes.domain

import com.maguro.recipes.data.repository.RecipeRepository
import javax.inject.Inject

interface ReloadRecipesUseCase {
    operator fun invoke(id: String? = null)
}

class ReloadRecipesUseCaseImpl @Inject constructor(
    private val recipeRepository: RecipeRepository
) : ReloadRecipesUseCase {
    override fun invoke(id: String?) {
        if (id == null) {
            recipeRepository.reload()
        } else {
            recipeRepository.reloadWithId(id)
        }
    }
}