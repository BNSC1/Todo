package com.bn.todo.data

data class Resource<out T>(val state: State, val data: T? = null, val message: String? = null) {
    companion object {
        fun <T> success(data: T?): Resource<T> =
            Resource(state = State.SUCCESS, data = data)

        fun <T> error(data: T?, message: String): Resource<T> =
            Resource(state = State.ERROR, data = data, message = message)

        fun loading(): Resource<Nothing> =
            Resource(state = State.LOADING)
    }
}