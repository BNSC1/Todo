package com.bn.todo.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.bn.todo.arch.BaseViewModel
import com.bn.todo.data.Resource
import com.bn.todo.data.State
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoFilter
import com.bn.todo.data.model.TodoList
import com.bn.todo.data.repository.TodoRepository
import com.bn.todo.data.repository.UserPrefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val userPrefRepository: UserPrefRepository
) : BaseViewModel() {

    private val _shouldGoToNewList = MutableStateFlow(false)
    val shouldGoToNewList = _shouldGoToNewList.asStateFlow()
    private val _shouldRefreshList = MutableStateFlow(false)
    val shouldRefreshList = _shouldRefreshList.asStateFlow()
    private val _clickedTodo = MutableStateFlow<Todo?>(null)
    val clickedTodo get() = _clickedTodo
    private val _listCount = MutableStateFlow(-1)
    val listCount get() = _listCount

    fun insertTodoList(name: String) = tryRun {
        todoRepository.insertTodoList(name)
        if (userPrefRepository.getIsFirstTimeLaunch().first()) {
            userPrefRepository.setIsFirstTimeLaunch(false)
        }
        setShouldGoToNewList()
    }

    fun queryTodoList(name: String? = null) =
        todoRepository.queryTodoList(name).onEach { _listCount.value = it.size }

    fun updateTodoList(list: TodoList, name: String) = tryRun {
        todoRepository.updateTodoList(list, name)
        setShouldRefreshList()
    }

    fun deleteTodoList(list: TodoList) = tryRun {
        todoRepository.deleteTodoList(list)
        setShouldGoToNewList()
    }

    fun insertTodo(title: String, body: String?) = tryRun {
        todoRepository.insertTodo(title, body, getCurrentListId().first())
        setShouldRefreshList()
    }

    suspend fun queryTodo(name: String? = null) =
        todoRepository.queryTodo(
            TodoFilter(
                getCurrentListId().first(),
                getShowCompleted().first()
            ).apply { this.name = name },
            getSortPref().first()
        )

    fun updateTodo(todo: Todo, name: String, body: String) = tryRun {
        todoRepository.updateTodo(todo, name, body)
        setShouldRefreshList()
    }

    fun updateTodo(todo: Todo, isCompleted: Boolean) = tryRun {
        todoRepository.updateTodo(todo, isCompleted)
        setShouldRefreshList()
    }

    fun deleteTodo(todo: Todo) = tryRun {
        todoRepository.deleteTodo(todo)
        setShouldRefreshList()
    }

    fun deleteCompletedTodos() = flow {
        val deleted = todoRepository.deleteCompletedTodo(getCurrentListId().first())
        emit(Resource.success(deleted))
    }.onEach {
        if (it.state == State.SUCCESS) {
            setShouldRefreshList()
        }
    }

    fun setClickedTodo(todo: Todo) {
        _clickedTodo.value = todo
    }

    fun setShouldRefreshList(value: Boolean = true) {
        _shouldRefreshList.value = value
    }

    fun setShouldGoToNewList(value: Boolean = true) {
        _shouldGoToNewList.value = value
    }

    fun getCurrentListId() = userPrefRepository.getCurrentListId(0)

    fun setCurrentListId(id: Int? = null) = viewModelScope.launch {
        id?.let { userPrefRepository.setCurrentListId(it) } ?: queryTodoList().first().firstOrNull()
    }

    fun getShowCompleted(default: Boolean = true) =
        userPrefRepository.getShowCompleted(default)

    fun setShowCompleted(showCompleted: Boolean) = viewModelScope.launch {
        userPrefRepository.setShowCompleted(showCompleted).also {
            setShouldRefreshList()
        }
    }

    suspend fun getCurrentList() =
        queryTodoList().first().firstOrNull { it.id == getCurrentListId().first() }

    fun setSortPref(sortPref: Int) {
        job = viewModelScope.launch {
            userPrefRepository.setSortPref(sortPref).also {
                setShouldRefreshList()
            }
        }
    }

    fun getSortPref(default: Int = 0) =
        userPrefRepository.getSortPref(default)
}