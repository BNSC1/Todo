package com.bn.todo.ui.main.viewmodel

import androidx.lifecycle.viewModelScope
import com.bn.todo.arch.BaseViewModel
import com.bn.todo.arch.HasListAction
import com.bn.todo.arch.ViewModelMessage
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoSort
import com.bn.todo.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TodoShowViewModel @Inject constructor(
    private val getShowCompletedFlowUseCase: GetShowCompletedFlowUseCase,
    private val getSortPrefFlowUseCase: GetSortPrefFlowUseCase,
    private val setSortPrefUseCase: SetSortPrefUseCase,
    private val getTodosFlowUseCase: GetTodosFlowUseCase,
    private val setShowCompletedUseCase: SetShowCompletedUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteCompletedTodosUseCase: DeleteCompletedTodosUseCase,
    private val insertTodoUseCase: InsertTodoUseCase
) : BaseViewModel(), HasListAction {

    private val _sortPref: StateFlow<TodoSort>
    val sortPref get() = _sortPref
    private val _currentTodos: StateFlow<List<Todo>>
    val currentTodos get() = _currentTodos
    private val _showCompleted: StateFlow<Boolean>
    val showCompleted get() = _showCompleted
    private val _todoQuery = MutableStateFlow("")
    val todoQuery get() = _todoQuery

    private val _clickedTodo = MutableStateFlow<Todo?>(null)
    val clickedTodo get() = _clickedTodo

    init {
        _sortPref = getSortPrefFlow()
        _currentTodos = queryTodo()
        _showCompleted = getShowCompletedFlow()
    }

    fun insertTodo(currentListId: Long?, title: String, body: String? = null) = tryRun {
        tryListAction(currentListId) {
            insertTodoUseCase(
                it,
                title,
                body
            )
        }
    }


    private fun queryTodo() =
        getTodosFlowUseCase(todoQuery)
            .catch {
                _message.emit(ViewModelMessage.Error(it.message.toString()))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )

    fun updateTodo(todo: Todo, name: String, body: String?) = tryRun {
        updateTodoUseCase(todo, name, body)
    }

    fun updateTodo(todo: Todo, isCompleted: Boolean) = tryRun {
        updateTodoUseCase(todo, isCompleted)
    }

    fun deleteTodo(todo: Todo) = tryRun {
        deleteTodoUseCase(todo)
    }

    fun deleteCompletedTodos(currentListId: Long?) = tryRun {
        val deletedTodoCount = tryListAction(currentListId) { deleteCompletedTodosUseCase(it) }
        _message.emit(deletedTodoCount.let {
            ViewModelMessage.Info.CompletedTodoDeletion(it as Int)
        }
        )
    }

    fun setClickedTodo(todo: Todo) {
        _clickedTodo.value = todo
    }

    fun searchTodo(query: String) {
        _todoQuery.value = query
    }

    private fun getShowCompletedFlow(default: Boolean = true) =
        getShowCompletedFlowUseCase(default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = false
            )

    fun setShowCompleted(showCompleted: Boolean) = tryRun {
        setShowCompletedUseCase(showCompleted)
    }

    fun setSortPref(sortPref: Int) = tryRun {
        setSortPrefUseCase(sortPref)
    }

    private fun getSortPrefFlow(default: Int = 0) =
        getSortPrefFlowUseCase(default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = TodoSort.values()[0]
            )
}