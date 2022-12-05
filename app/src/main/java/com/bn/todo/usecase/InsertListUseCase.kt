package com.bn.todo.usecase

import com.bn.todo.data.repository.TodoRepository
import javax.inject.Inject

class InsertListUseCase @Inject constructor(private val repository: TodoRepository) {
    suspend operator fun invoke(name: String) {
        repository.insertTodoList(name)
    }
}