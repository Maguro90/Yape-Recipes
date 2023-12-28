package com.maguro.recipes.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.coroutines.CoroutineContext

fun Flow<Signal>.filterSignal(predicate: (Signal) -> Boolean): Flow<Signal> =
    filter { it is Signal.InitialLoad || predicate(it) }

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<Signal>.asRequestResultFlow(ioDispatcher: CoroutineContext, block: suspend () -> T) =
    map {
        when (it) {
            is Signal.InitialLoad -> RequestResult.FirstLoad
            else -> RequestResult.Refresh
        }
    }
    .flatMapLatest { lastResult ->
        flow {
            emit(RequestResult.Success(block()))
        }
        .flowOn(ioDispatcher)
        .stateIn(
            scope = MainScope(),
            started = SharingStarted.Eagerly,
            initialValue = lastResult
        )
    }
    .catchAndConvertErrors()

fun <T> Flow<RequestResult<T>>.catchAndConvertErrors() = catch {
    when (it) {
        is HttpException -> RequestResult.Error.Server(it)
        is SocketTimeoutException -> RequestResult.Error.Timeout(it)
        is IOException -> RequestResult.Error.Connection(it)
        else -> RequestResult.Error.Unknown(it)
    }.also { signal ->
        emit(signal)
    }
}

fun <T, R> Flow<RequestResult<T>>.mapResult(block: suspend (T) -> R): Flow<RequestResult<R>> =
    map { signal ->
        when (signal) {
            is RequestResult.FirstLoad -> signal
            is RequestResult.Refresh -> signal
            is RequestResult.Error -> signal
            is RequestResult.Success -> RequestResult.Success(block(signal.data))
        }
    }