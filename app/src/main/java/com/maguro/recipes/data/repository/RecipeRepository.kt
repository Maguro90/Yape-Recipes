package com.maguro.recipes.data.repository

import android.util.Log
import com.maguro.recipes.data.model.Recipe
import com.maguro.recipes.data.remote.RecipesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    private val ioDispatcher: CoroutineContext
) : RecipeRepository {

    @Inject
    constructor(
        recipesApi: RecipesApi,
    ): this(
        recipesApi = recipesApi,
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

    init {
        signalingFlow.subscriptionCount
            .onEach {
                Log.e("SignalFlow", "$it")
            }
            .launchIn(MainScope())
    }


    override val all: Flow<RequestResult<List<Recipe>>> =
        defaultSignalingFlow
            .onEach {
                Log.e("Hola", "Hola $it")
            }
            .filterSignal { it is Signal.ReloadAll }
            .asRequestResultFlow(ioDispatcher) {
                recipesApi.fetchAll()
            }

    override fun getById(id: String): Flow<RequestResult<Recipe?>> =
        defaultSignalingFlow
            .filterSignal {
                Log.e("Signal", it.toString())
                it is Signal.ReloadWithId && it.id == id
            }
            .asRequestResultFlow(ioDispatcher) {
                recipesApi.fetchById(id)
            }

    override fun reload() {
        signalingFlow.tryEmit(Signal.ReloadAll)
    }

    override fun reloadWithId(id: String) {
        signalingFlow.tryEmit(Signal.ReloadWithId(id))
    }

}

