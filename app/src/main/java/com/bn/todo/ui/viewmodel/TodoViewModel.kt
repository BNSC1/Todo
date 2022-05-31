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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    private fun queryTodoList(name: String? = null) =
        todoRepository.queryTodoList(name).onEach { listCount.emit(it.size) }

    fun updateTodoList(list: TodoList, name: String) = stateFlow {
        todoRepository.updateTodoList(list, name)
        emit(Resource.success(null))
    }

    fun deleteTodoList(list: TodoList) = flow {
        todoRepository.deleteTodoList(list)
        emit(Resource.success(null))
    }.onEach {
        if (it.state == State.SUCCESS) {
            setShouldGoToNewList()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    fun insertTodo(title: String, body: String?) = flow {
        todoRepository.insertTodo(title, body, getCurrentListId().first())
        emit(Resource.success(null))
    }.onEach {
        if (it.state == State.SUCCESS) {
            setShouldRefreshList()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    fun updateTodo(todo: Todo, isCompleted: Boolean) = stateFlow {
        todoRepository.updateTodo(todo, isCompleted)
        emit(Resource.success(null))
    }

    fun deleteTodo(todo: Todo) = flow {
        todoRepository.deleteTodo(todo)
        emit(Resource.success(null))
    }.onEach {
        setShouldRefreshList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    fun deleteCompletedTodos() = flow {
        val deleted = todoRepository.deleteCompletedTodo(getCurrentListId().first())
        emit(Resource.success(deleted))
    }.onEach { setShouldRefreshList() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    suspend fun setShouldRefreshList(value: Boolean = true) = _shouldRefreshList.emit(value)

    suspend fun setShouldGoToNewList(value: Boolean = true) = _shouldGoToNewList.emit(value)

    fun getCurrentListId() = userPrefRepository.getCurrentListId(0)

    suspend fun setCurrentListId(id: Int? = null) =
        id?.let { userPrefRepository.setCurrentListId(it) } ?: todoLists.first()[0]

    fun getShowCompleted(default: Boolean = true) =
        userPrefRepository.getShowCompleted(default)

    suspend fun setShowCompleted(showCompleted: Boolean) =
        userPrefRepository.setShowCompleted(showCompleted).also {
            setShouldRefreshList()
        }

    suspend fun getCurrentList() =
        todoLists.first().firstOrNull { it.id == getCurrentListId().first() }

    suspend fun setSortPref(sortPref: Int) =
        userPrefRepository.setSortPref(sortPref).also {
            setShouldRefreshList()
        }

    fun getSortPref(default: Int = 0) =
        userPrefRepository.getSortPref(default)
}