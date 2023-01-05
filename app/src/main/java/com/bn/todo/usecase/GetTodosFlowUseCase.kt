package com.bn.todo.usecase

import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoFilter
import com.bn.todo.data.model.TodoSort.*
import com.bn.todo.data.repository.TodoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetTodosFlowUseCase @Inject constructor(
    private val todoRepository: TodoRepository,
    private val getCurrentListIdFlowUseCase: GetCurrentListIdFlowUseCase,
    private val getShowCompletedFlowUseCase: GetShowCompletedFlowUseCase,
    private val getSortPrefFlowUseCase: GetSortPrefFlowUseCase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(todoQuery: StateFlow<String>): Flow<List<Todo>> {
        val filterFlow = combine(
            getCurrentListIdFlowUseCase(),
            getShowCompletedFlowUseCase(),
            todoQuery
        ) { id, showCompleted, query ->
            TodoFilter(id, showCompleted, query)
        }.distinctUntilChanged()

        return filterFlow.flatMapLatest { filter ->
            todoRepository.queryTodoFlow(filter)
        }.combine(getSortPrefFlowUseCase()) { todos, sortPref ->
            with(todos) {
                when (sortPref) {
                    ORDER_ADDED -> sortedBy { it.id }
                    ORDER_NOT_COMPLETED -> sortedBy { it.isCompleted }
                    ORDER_ALPHABET -> sortedBy { it.title }
                }
            }
        }
    }
}