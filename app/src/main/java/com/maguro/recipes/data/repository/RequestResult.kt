package com.maguro.recipes.data.repository

sealed interface RequestResult<out T> {

    object FirstLoad: RequestResult<Nothing>
    object Refresh: RequestResult<Nothing>
    class Success<T>(val data: T): RequestResult<T>

    sealed interface Error: RequestResult<Nothing> {
        val error: Throwable
        class Connection(override val error: Throwable): Error
        class Server(override val error: Throwable): Error
        class Timeout(override val error: Throwable): Error
        class Unknown(override val error: Throwable): Error
    }
}
