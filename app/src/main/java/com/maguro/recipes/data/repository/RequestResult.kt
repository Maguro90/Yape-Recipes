package com.maguro.recipes.data.repository

sealed interface RequestResult<out T> {

    object FirstLoad: RequestResult<Nothing>

    sealed class WithData<T>: RequestResult<T> {
        abstract val data: T
        abstract val error: ErrorType

        private var isErrorConsumed: Boolean = false

        fun consumeError(): ErrorType {
            if (isErrorConsumed)
                return ErrorType.None
            isErrorConsumed = true
            return error
        }

        data class Refresh<T>(
            override val data: T,
            override val error: ErrorType
        ): WithData<T>()

        data class Loaded<T>(
            override val data: T,
            override val error: ErrorType
        ): WithData<T>()
    }

}


sealed interface ErrorType {
    object None: ErrorType
    class Connection(val cause: Throwable): ErrorType
    class Server(val code: Int, val cause: Throwable): ErrorType
    class ConnectionTimeout(val cause: Throwable): ErrorType
    class Unknown(val cause: Throwable): ErrorType
}
