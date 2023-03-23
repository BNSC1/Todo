package com.bn.todo.usecase

import com.bn.todo.data.repository.TodoRepository
import javax.inject.Inject

class InsertTodoListUseCase @Inject constructor(private val repository: TodoRepository) {
    suspend operator fun invoke(name: String): Long {
        if (name.isEmpty()) throw IllegalArgumentException("Empty todo list name")
        return repository.insertTodoList(name)
    }
}