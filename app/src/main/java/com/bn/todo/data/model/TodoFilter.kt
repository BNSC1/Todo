package com.bn.todo.data.model

data class TodoFilter(
    val listId: Int,
    val showCompleted: Boolean
) {
    var name: String? = null
}
