package com.bn.todo.arch

import com.bn.todo.data.model.TodoList

interface HasListAction {
    suspend fun tryListAction(list: TodoList?, action: suspend (Long) -> Any) =
        list?.let {
            action(it.id)
        } ?: throw IllegalArgumentException("List is null.")
}
