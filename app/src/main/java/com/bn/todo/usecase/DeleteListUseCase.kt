package com.bn.todo.usecase

import com.bn.todo.data.model.TodoList
import com.bn.todo.data.repository.TodoRepository
import javax.inject.Inject

class DeleteListUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(list: TodoList) {
        repository.deleteTodoList(list)
    }
}