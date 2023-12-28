package com.maguro.recipes.di

import com.maguro.recipes.domain.GetAllRecipesSortedByNameUseCase
import com.maguro.recipes.domain.GetAllRecipesSortedByNameUseCaseImpl
import com.maguro.recipes.domain.GetRecipeByIdUseCase
import com.maguro.recipes.domain.GetRecipeByIdUseCaseImpl
import com.maguro.recipes.domain.ReloadRecipesUseCase
import com.maguro.recipes.domain.ReloadRecipesUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindGetAllRecipesSortedByNameUseCase(
        impl: GetAllRecipesSortedByNameUseCaseImpl
    ): GetAllRecipesSortedByNameUseCase

    @Binds
    abstract fun bindReloadRecipesUseCase(
        impl: ReloadRecipesUseCaseImpl
    ): ReloadRecipesUseCase

    @Binds
    abstract fun bindGetRecipeByIdUseCase(
        impl: GetRecipeByIdUseCaseImpl
    ): GetRecipeByIdUseCase
}