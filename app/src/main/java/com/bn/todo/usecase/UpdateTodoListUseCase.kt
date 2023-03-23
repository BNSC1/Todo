package com.bn.todo.usecase

import com.bn.todo.data.model.TodoList
import com.bn.todo.data.repository.TodoRepository
import javax.inject.Inject

class UpdateTodoListUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(list: TodoList, name: String) {
        if (name.isEmpty()) throw IllegalArgumentException("Empty todo list name")
        repository.updateTodoList(list, name)
    }
}