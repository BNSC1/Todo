package com.bn.todo.data.model

data class TodoFilter(
    val listId: Long,
    val showCompleted: Boolean
) {
    var name: String? = null
}
