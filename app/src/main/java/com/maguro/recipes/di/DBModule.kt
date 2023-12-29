package com.maguro.recipes.di

import android.content.Context
import androidx.room.Room
import com.maguro.recipes.data.local.RecipeDatabase
import com.maguro.recipes.data.local.dao.RecipesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    @Singleton
    @Provides
    fun provideDBInstance(
        @ApplicationContext
        context: Context
    ): RecipeDatabase =
        Room.databaseBuilder(
            context,
            RecipeDatabase::class.java,
            "local-store"
        ).build()

    @Singleton
    @Provides
    fun provideRecipeDao(
        db: RecipeDatabase
    ): RecipesDao = db.recipeDao()


}