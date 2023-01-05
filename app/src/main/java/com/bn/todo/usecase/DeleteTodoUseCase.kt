package com.bn.todo.usecase

import com.bn.todo.data.model.Todo
import com.bn.todo.data.repository.TodoRepository
import javax.inject.Inject

class DeleteTodoUseCase @Inject constructor(private val repository: TodoRepository) {
    suspend operator fun invoke(todo: Todo) = repository.deleteTodo(todo)
}
