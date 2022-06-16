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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val userPrefRepository: UserPrefRepository
) : BaseViewModel() {

    private val _shouldGoToNewList = MutableStateFlow(false)
    val shouldGoToNewList = _shouldGoToNewList.asStateFlow()
    private val _shouldRefreshList = MutableSharedFlow<Boolean>(replay = 1)
    val shouldRefreshList = _shouldRefreshList.asSharedFlow()
    private val _clickedTodo = MutableSharedFlow<Todo>(replay = 1)
    val clickedTodo get() = _clickedTodo
    private val _listCount = MutableStateFlow(-1)
    val listCount get() = _listCount
    val todoLists get() = queryTodoList()

    fun insertTodoList(name: String) = flow {
        todoRepository.insertTodoList(name)
        emit(Resource.success(null))
    }.onEach {
        if (!userPrefRepository.getNotFirstTimeLaunch().first()) {
            userPrefRepository.setNotFirstTimeLaunch(true)
        }

        if (it.state == State.SUCCESS) {
            setShouldGoToNewList()
        }
    }

    private fun queryTodoList(name: String? = null) =
        todoRepository.queryTodoList(name).onEach { listCount.emit(it.size) }

    fun updateTodoList(list: TodoList, name: String) = flow {
        todoRepository.updateTodoList(list, name)
        emit(Resource.success(null))
    }.onEach {
        if (it.state == State.SUCCESS) {
            setShouldGoToNewList()
        }
    }

    fun deleteTodoList(list: TodoList) = flow {
        todoRepository.deleteTodoList(list)
        emit(Resource.success(null))
    }.onEach {
        if (it.state == State.SUCCESS) {
            setShouldGoToNewList()
        }
    }

    fun insertTodo(title: String, body: String?) = flow {
        todoRepository.insertTodo(title, body, getCurrentListId().first())
        emit(Resource.success(null))
    }.onEach {
        if (it.state == State.SUCCESS) {
            setShouldRefreshList()
        }
    }

    suspend fun queryTodo(name: String? = null) =
        todoRepository.queryTodo(
            TodoFilter(
                getCurrentListId().first(),
                getShowCompleted().first()
            ).apply { this.name = name },
            getSortPref().first()
        )

    fun updateTodo(todo: Todo, name: String, body: String) = flow {
        todoRepository.updateTodo(todo, name, body)
        emit(Resource.success(null))
    }.onEach {
        if (it.state == State.SUCCESS) {
            setShouldRefreshList()
        }
    }

    fun updateTodo(todo: Todo, isCompleted: Boolean) = flow {
        todoRepository.updateTodo(todo, isCompleted)
        emit(Resource.success(null))
    }.onEach {
        if (it.state == State.SUCCESS) {
            setShouldRefreshList()
        }
    }

    fun deleteTodo(todo: Todo) = flow {
        todoRepository.deleteTodo(todo)
        emit(Resource.success(null))
    }.onEach {
        if (it.state == State.SUCCESS) {
            setShouldRefreshList()
        }
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
        job = viewModelScope.launch {
            _clickedTodo.emit(todo)
        }
    }

    fun setShouldRefreshList(value: Boolean = true) {
        job = viewModelScope.launch {
            _shouldRefreshList.emit(value)
        }
    }

    fun setShouldGoToNewList(value: Boolean = true) {
        job = viewModelScope.launch {
            _shouldGoToNewList.emit(value)
        }
    }

    fun getCurrentListId() = userPrefRepository.getCurrentListId(0)

    fun setCurrentListId(id: Int? = null) {
        job = viewModelScope.launch {
            id?.let { userPrefRepository.setCurrentListId(it) } ?: todoLists.first()[0]
        }
    }

    fun getShowCompleted(default: Boolean = true) =
        userPrefRepository.getShowCompleted(default)

    fun setShowCompleted(showCompleted: Boolean) {
        job = viewModelScope.launch {
            userPrefRepository.setShowCompleted(showCompleted).also {
                setShouldRefreshList()
            }
        }
    }

    suspend fun getCurrentList() =
        todoLists.first().firstOrNull { it.id == getCurrentListId().first() }

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