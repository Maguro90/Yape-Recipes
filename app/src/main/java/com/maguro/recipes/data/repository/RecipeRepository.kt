package com.maguro.recipes.data.repository

import com.maguro.recipes.data.local.dao.RecipesDao
import com.maguro.recipes.data.model.Recipe
import com.maguro.recipes.data.remote.RecipesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onSubscription
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

interface RecipeRepository {

    val all: Flow<RequestResult<List<Recipe>>>
    fun getById(id: String): Flow<RequestResult<Recipe?>>

    fun reload()

    fun reloadWithId(id: String)
}

@Singleton
class RecipeRepositoryImpl (
    private val recipesApi: RecipesApi,
    private val recipesDao: RecipesDao,
    private val ioDispatcher: CoroutineContext
) : RecipeRepository {

    @Inject
    constructor(
        recipesApi: RecipesApi,
        recipesDao: RecipesDao
    ): this(
        recipesApi = recipesApi,
        recipesDao = recipesDao,
        ioDispatcher = Dispatchers.IO
    )

    private val signalingFlow =
        MutableSharedFlow<Signal>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_LATEST
        )

    private val defaultSignalingFlow =
        signalingFlow
            .onSubscription {
                emit(Signal.InitialLoad)
            }

    override val all: Flow<RequestResult<List<Recipe>>> =
        defaultSignalingFlow
            .filterSignal { it is Signal.ReloadAll }
            .asResultRequestFlow(
                ioDispatcher = ioDispatcher,
                localFetcher = { recipesDao.getAll() },
                localSaver = { recipesDao.insertRecipes(it) },
                localDataValidator = { it.isNotEmpty() },
                remoteFetcher = { recipesApi.fetchAll() }
            )

    override fun getById(id: String): Flow<RequestResult<Recipe?>> =
        defaultSignalingFlow
            .filterSignal {
                it is Signal.ReloadWithId && it.id == id
            }
            .asResultRequestFlow(
                ioDispatcher = ioDispatcher,
                localFetcher = { recipesDao.getById(id) },
                localSaver = { it?.also { recipesDao.insertRecipe(it) } },
                localDataValidator = { it != null },
                remoteFetcher = { recipesApi.fetchById(id) }
            )


    override fun reload() {
        signalingFlow.tryEmit(Signal.ReloadAll)
    }

    override fun reloadWithId(id: String) {
        signalingFlow.tryEmit(Signal.ReloadWithId(id))
    }

}

