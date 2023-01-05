package com.bn.todo.ui.main.viewmodel

import androidx.lifecycle.viewModelScope
import com.bn.todo.arch.BaseViewModel
import com.bn.todo.data.model.TodoList
import com.bn.todo.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val insertTodoListUseCase: InsertTodoListUseCase,
    private val getTodoListFlowUseCase: GetTodoListFlowUseCase,
    private val updateTodoListUseCase: UpdateTodoListUseCase,
    private val deleteTodoListUseCase: DeleteTodoListUseCase,
    private val getCurrentListIdFlowUseCase: GetCurrentListIdFlowUseCase,
    private val setCurrentListIdUseCase: SetCurrentListIdUseCase
) : BaseViewModel() {
    private val _todoLists: StateFlow<List<TodoList>>
    val todoLists get() = _todoLists
    private val _currentList: StateFlow<TodoList?>
    val currentList get() = _currentList

    init {
        _todoLists = getTodoListFlow()
        _currentList = getCurrentListFlow()
    }

    private fun getTodoListFlow(query: String? = null) =
        getTodoListFlowUseCase(query).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun insertTodoList(name: String) = tryRun {
        setCurrentList(id = insertTodoListUseCase(name))
    }

    fun updateTodoList(list: TodoList, name: String) {
        tryRun {
            updateTodoListUseCase(list, name)
        }
    }

    fun deleteTodoList(list: TodoList) {
        tryRun {
            val lists = todoLists.value
            if (lists.size > 1) {
                setCurrentList(
                    lists[if (lists[0] == list) 1 else 0]
                )
                deleteTodoListUseCase(list)
            } else throw IllegalStateException("Attempting to delete last todo list")
        }
    }

    fun setCurrentList(ordinal: Int) {
        setCurrentList(todoLists.value[ordinal].id)
    }

    private fun setCurrentList(list: TodoList) =
        setCurrentList(list.id)


    private fun setCurrentList(id: Long) = tryRun {
        setCurrentListIdUseCase(id)
    }

    private fun getCurrentListFlow() = todoLists.combine(getCurrentListIdFlow()) { list, id ->
        list.firstOrNull { it.id == id } ?: todoLists.value.firstOrNull()
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    private fun getCurrentListIdFlow() = getCurrentListIdFlowUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0
        )
}