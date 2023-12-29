package com.maguro.recipes.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.CoroutineContext

fun Flow<Signal>.filterSignal(predicate: (Signal) -> Boolean): Flow<Signal> =
    filter { it is Signal.InitialLoad || predicate(it) }

fun <T, R> Flow<RequestResult<T>>.mapResult(block: suspend (T) -> R): Flow<RequestResult<R>> =
    map { signal ->
        when (signal) {
            is RequestResult.FirstLoad -> signal
            is RequestResult.WithData -> {
                when (signal) {
                    is RequestResult.WithData.Refresh ->
                        RequestResult.WithData.Refresh(block(signal.data), signal.error)

                    is RequestResult.WithData.Loaded ->
                        RequestResult.WithData.Loaded(block(signal.data), signal.error)
                }
            }
        }
    }

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<Signal>.asResultRequestFlow(
    ioDispatcher: CoroutineContext,
    localFetcher: suspend () -> T,
    localSaver: suspend (T) -> Unit,
    localDataValidator: (T) -> Boolean,
    remoteFetcher: suspend () -> T
): Flow<RequestResult<T>> =
    flatMapLatest { signal ->
        flow {
            val localData = localFetcher()

            if (signal is Signal.InitialLoad) {
                if (localDataValidator(localData)) {
                    emit(RequestResult.WithData.Loaded(localData, ErrorType.None))
                    return@flow
                }
                emit(RequestResult.FirstLoad)
            } else {
                emit(
                    RequestResult.WithData.Refresh(
                        localData,
                        ErrorType.None
                    )
                )
            }

            val error = convertError { localSaver(remoteFetcher()) }

            emit(RequestResult.WithData.Loaded(localFetcher(), error))
        }
        .flowOn(ioDispatcher)
    }

suspend fun convertError(block: suspend () -> Unit): ErrorType =
    try {
        block()
        ErrorType.None
    } catch (e: HttpException) {
        ErrorType.Server(e.code(), e)
    } catch (e: SocketTimeoutException) {
        ErrorType.ConnectionTimeout(e)
    } catch (e: UnknownHostException) {
        ErrorType.Connection(e)
    } catch (e: Throwable) {
        ErrorType.Unknown(e)
    }
