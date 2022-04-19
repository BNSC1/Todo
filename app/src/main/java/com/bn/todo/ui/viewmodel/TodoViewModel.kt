package com.bn.todo.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.bn.todo.arch.BaseViewModel
import com.bn.todo.data.Resource
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoFilter
import com.bn.todo.data.model.TodoList
import com.bn.todo.data.repository.TodoRepository
import com.bn.todo.util.DataStoreKeys
import com.bn.todo.util.DataStoreMgr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
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
        repository.insertTodoList(name)
        if (!getNotFirstTimeLaunch().first()) {
            setNotFirstTimeLaunch(true)
        }
        emit(Resource.success(null))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    private fun queryTodoList(name: String? = null) = repository.queryTodoList(name)

    fun updateTodoList(list: TodoList, name: String) = flow {
        repository.updateTodoList(list, name)
        emit(Resource.success(null))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    fun deleteTodoList(list: TodoList) = flow {
        repository.deleteTodoList(list)
        emit(Resource.success(null))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    fun insertTodo(title: String, body: String?) = flow {
        repository.insertTodo(title, body, getCurrentListId().first())
        emit(Resource.success(null))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    suspend fun queryTodo(name: String? = null) =
        repository.queryTodo(
            TodoFilter(
                getCurrentListId().first(),
                getShowCompleted().first()
            ).apply { this.name = name })

    fun updateTodo(todo: Todo, name: String, body: String) = flow {
        repository.updateTodo(todo, name, body)
        emit(Resource.success(null))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    fun updateTodo(todo: Todo, isCompleted: Boolean) = flow {
        repository.updateTodo(todo, isCompleted)
        emit(Resource.success(null))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    fun deleteTodo(todo: Todo) = flow {
        repository.deleteTodo(todo)
        emit(Resource.success(null))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    fun deleteCompletedTodos() = flow {
        val deleted = repository.deleteCompletedTodo(getCurrentListId().first())
        emit(Resource.success(deleted))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    suspend fun setShowCompleted(showCompleted: Boolean) =
        DataStoreMgr.setPreferences(DataStoreKeys.SHOW_COMPLETED, showCompleted)

    suspend fun getShowCompleted(default: Boolean = true) =
        DataStoreMgr.getPreferences(DataStoreKeys.SHOW_COMPLETED, default)

    private suspend fun setNotFirstTimeLaunch(isNotFirstTimeLaunch: Boolean) =
        DataStoreMgr.setPreferences(DataStoreKeys.NOT_FIRST_LAUNCH, isNotFirstTimeLaunch)

    private suspend fun getNotFirstTimeLaunch(default: Boolean = false) =
        DataStoreMgr.getPreferences(DataStoreKeys.NOT_FIRST_LAUNCH, default)

    suspend fun getCurrentList() = todoLists.first().first { it.id == getCurrentListId().first() }

    suspend fun getCurrentListId() =
        DataStoreMgr.getPreferences(DataStoreKeys.CURRENT_LIST, todoLists.first()[0].id)

    suspend fun setCurrentListId(id: Int) = withContext(Dispatchers.IO) {
        DataStoreMgr.setPreferences(DataStoreKeys.CURRENT_LIST, id)
    }
}