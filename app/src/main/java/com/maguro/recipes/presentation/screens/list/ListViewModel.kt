package com.maguro.recipes.presentation.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maguro.recipes.data.model.Recipe
import com.maguro.recipes.data.repository.RequestResult
import com.maguro.recipes.domain.GetAllRecipesSortedByNameUseCase
import com.maguro.recipes.domain.ReloadRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getAllRecipesSortedByNameUseCase: GetAllRecipesSortedByNameUseCase,
    private val reloadRecipesUseCase: ReloadRecipesUseCase
) : ViewModel() {

    val recipes: StateFlow<RequestResult<List<Recipe>>> =
        getAllRecipesSortedByNameUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = RequestResult.FirstLoad
            )

    fun reload() {
        reloadRecipesUseCase()
    }

}