package com.bn.todo.usecase

import com.bn.todo.data.repository.TodoRepository
import javax.inject.Inject

class DeleteCompletedTodosUseCase @Inject constructor(private val repository: TodoRepository) {
    suspend operator fun invoke(listId: Long) = repository.deleteCompletedTodo(listId)
}
