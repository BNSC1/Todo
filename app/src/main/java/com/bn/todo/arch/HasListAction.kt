package com.bn.todo.arch

interface HasListAction {
    suspend fun tryListAction(listId: Long?, action: suspend (Long) -> Any) =
        listId?.let {
            action(it)
        } ?: throw IllegalArgumentException("List ID is null.")
}
