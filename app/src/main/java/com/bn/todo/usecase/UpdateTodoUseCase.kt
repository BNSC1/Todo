package com.bn.todo.usecase

import com.bn.todo.data.model.Todo
import com.bn.todo.data.repository.TodoRepository
import javax.inject.Inject

class UpdateTodoUseCase @Inject constructor(private val repository: TodoRepository) {
    suspend operator fun invoke(todo: Todo, name: String, body: String?) =
        repository.updateTodo(todo, name, body)

    suspend operator fun invoke(todo: Todo, isCompleted: Boolean) =
        repository.updateTodo(todo, isCompleted)
}
