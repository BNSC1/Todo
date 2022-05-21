package com.bn.todo.data

data class Resource<out T>(
    val state: State,
    val data: T? = null,
    val message: String? = null,
    val messageResId: Int? = null,
) {
    companion object {
        fun <T> success(data: T?): Resource<T> =
            Resource(state = State.SUCCESS, data = data)

        fun <T> error(data: T?, message: String? = null, messageResId: Int? = null): Resource<T> =
            Resource(
                state = State.ERROR,
                data = data,
                message = message,
                messageResId = messageResId
            )

        fun loading(): Resource<Nothing> =
            Resource(state = State.LOADING)
    }
}