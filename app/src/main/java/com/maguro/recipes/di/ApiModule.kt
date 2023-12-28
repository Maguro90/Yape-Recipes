package com.maguro.recipes.di

import com.maguro.recipes.data.remote.RecipesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
            .build()
    @Provides
    fun provideRetrofit(
        client: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://656f81d06529ec1c6237f845.mockapi.io/recipes-api/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()

    @Provides
    fun provideRecipesApi(
        retrofit: Retrofit
    ): RecipesApi = retrofit.create(RecipesApi::class.java)
}