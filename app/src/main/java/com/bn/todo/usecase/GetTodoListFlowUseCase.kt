package com.bn.todo.usecase

import com.bn.todo.data.repository.TodoRepository
import javax.inject.Inject

class GetTodoListFlowUseCase @Inject constructor(private val repository: TodoRepository) {
    operator fun invoke(query: String?) =
        repository.queryTodoList(query)
}