package com.maguro.recipes.presentation.screens.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maguro.recipes.data.model.Recipe
import com.maguro.recipes.data.repository.RequestResult
import com.maguro.recipes.domain.GetRecipeByIdUseCase
import com.maguro.recipes.domain.ReloadRecipesUseCase
import com.maguro.recipes.presentation.navigation.RouteWithId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val reloadRecipesUseCase: ReloadRecipesUseCase,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val recipeId: String =
        requireNotNull(savedStateHandle.get<String>(RouteWithId.ID))

    val recipe: StateFlow<RequestResult<Recipe?>> =
        getRecipeByIdUseCase(recipeId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = RequestResult.FirstLoad
            )

    fun reload() {
        reloadRecipesUseCase(id = recipeId)
    }
}