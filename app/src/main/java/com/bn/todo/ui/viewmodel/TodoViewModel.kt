package com.bn.todo.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.bn.todo.arch.BaseViewModel
import com.bn.todo.data.Resource
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoFilter
import com.bn.todo.data.model.TodoList
import com.bn.todo.data.repository.TodoRepository
import com.bn.todo.data.repository.UserPrefRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val userPrefRepository: UserPrefRepository
) : BaseViewModel() {

    private val _shouldGoToNewList by lazy { MutableStateFlow(false) }
    val shouldGoToNewList get() = _shouldGoToNewList
    private val _shouldRefreshList by lazy { MutableSharedFlow<Boolean>(replay = 1) }
    val shouldRefreshList get() = _shouldRefreshList
    private val _shouldRefreshTitle by lazy { MutableSharedFlow<Boolean>(replay = 1) }
    val shouldRefreshTitle get() = _shouldRefreshTitle
    private val _clickedTodo by lazy { MutableSharedFlow<Todo>(replay = 1) }
    val clickedTodo get() = _clickedTodo
    val todoLists get() = queryTodoList()

    fun insertTodoList(name: String) = flow {
        todoRepository.insertTodoList(name)
        if (!userPrefRepository.getNotFirstTimeLaunch().first()) {
            userPrefRepository.setNotFirstTimeLaunch(true)
        }
        emit(Resource.success(null))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    private fun queryTodoList(name: String? = null) = todoRepository.queryTodoList(name)

    fun updateTodoList(list: TodoList, name: String) = flow {
        todoRepository.updateTodoList(list, name)
        emit(Resource.success(null))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    fun deleteTodoList(list: TodoList) = flow {
        todoRepository.deleteTodoList(list)
        emit(Resource.success(null))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    fun insertTodo(title: String, body: String?) = flow {
        todoRepository.insertTodo(title, body, getCurrentListId().first())
        emit(Resource.success(null))
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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    fun updateTodo(todo: Todo, isCompleted: Boolean) = flow {
        todoRepository.updateTodo(todo, isCompleted)
        emit(Resource.success(null))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    fun deleteTodo(todo: Todo) = flow {
        todoRepository.deleteTodo(todo)
        emit(Resource.success(null))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    fun deleteCompletedTodos() = flow {
        val deleted = todoRepository.deleteCompletedTodo(getCurrentListId().first())
        emit(Resource.success(deleted))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    suspend fun getCurrentListId() = userPrefRepository.getCurrentListId(todoLists.first()[0].id)

    suspend fun setCurrentListId(id: Int) = withContext(Dispatchers.IO) {
        userPrefRepository.setCurrentListId(id)
    }

    suspend fun getShowCompleted(default: Boolean = true) =
        userPrefRepository.getShowCompleted(default)

    suspend fun setShowCompleted(showCompleted: Boolean) =
        userPrefRepository.setShowCompleted(showCompleted)

    suspend fun getCurrentList() = todoLists.first().first { it.id == getCurrentListId().first() }

    suspend fun setSortPref(sortPref: Int) =
        userPrefRepository.setSortPref(sortPref)

    suspend fun getSortPref(default: Int = 0) =
        userPrefRepository.getSortPref(default)
}