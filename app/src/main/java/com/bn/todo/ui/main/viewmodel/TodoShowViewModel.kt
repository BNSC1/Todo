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
    private val setShowCompletedUseCase: SetShowCompletedUseCase
) : BaseViewModel(), HasListAction {

    val sortPref: StateFlow<TodoSort>
    val currentTodos: StateFlow<List<Todo>>
    val showCompleted: StateFlow<Boolean>
    private val _todoQuery = MutableStateFlow("")
    val todoQuery = _todoQuery.asStateFlow()

    init {
        sortPref = getSortPrefFlow()
        currentTodos = queryTodo()
        showCompleted = getShowCompletedFlow()
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