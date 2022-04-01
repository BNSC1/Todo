package com.bn.todo.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.bn.todo.arch.BaseViewModel
import com.bn.todo.data.Resource
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoList
import com.bn.todo.data.repository.TodoRepository
import com.bn.todo.util.DataStoreKeys
import com.bn.todo.util.DataStoreMgr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : BaseViewModel() {

    private val _shouldGoToNewList by lazy { MutableStateFlow(false) }
    val shouldGoToNewList get() = _shouldGoToNewList
    private val _shouldRefreshList by lazy { MutableSharedFlow<Boolean>() }
    val shouldRefreshList get() = _shouldRefreshList
    private val _shouldRefreshTitle by lazy { MutableSharedFlow<Boolean>() }
    val shouldRefreshTitle get() = _shouldRefreshTitle
    private val _clickedTodo by lazy { MutableSharedFlow<Todo>() }
    val clickedTodo get() = _clickedTodo

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

    fun queryTodoList(name: String? = null) = repository.queryTodoList(name)

    fun updateTodoList(list: TodoList, name: String) {}
    fun deleteTodoList(list: TodoList) {}

    fun insertTodo(title: String, body: String?) = flow {
        job = viewModelScope.launch {
            repository.insertTodo(title, body, getCurrentListId().first())
        }
        emit(Resource.success(null))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    suspend fun queryTodo(name: String? = null) =
        repository.queryTodo(getCurrentListId().first(), name)
//            .shareIn(scope = viewModelScope, started = SharingStarted.Lazily)

    fun updateTodo(todo: Todo, name: String, body: String) {
        job = viewModelScope.launch {
            repository.updateTodo(todo, name, body)
        }
    }

    fun deleteTodo(todo: Todo) {
        job = viewModelScope.launch {
            repository.deleteTodo(todo)
        }
    }

    private suspend fun setNotFirstTimeLaunch(isNotFirstTimeLaunch: Boolean) =
        DataStoreMgr.setPreferences(DataStoreKeys.NOT_FIRST_LAUNCH, isNotFirstTimeLaunch)

    private suspend fun getNotFirstTimeLaunch(default: Boolean = false) =
        DataStoreMgr.getPreferences(DataStoreKeys.NOT_FIRST_LAUNCH, default)

    suspend fun getCurrentListId() =
        DataStoreMgr.getPreferences(DataStoreKeys.CURRENT_LIST, 1)

    suspend fun setCurrentListId(id: Int) = withContext(Dispatchers.IO) {
        DataStoreMgr.setPreferences(DataStoreKeys.CURRENT_LIST, id)
    }
}