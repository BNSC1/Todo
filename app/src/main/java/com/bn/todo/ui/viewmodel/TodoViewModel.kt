package com.bn.todo.ui.viewmodel

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bn.todo.arch.BaseViewModel
import com.bn.todo.data.Resource
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoList
import com.bn.todo.data.repository.TodoRepository
import com.bn.todo.util.DataStoreKeys
import com.bn.todo.util.DataStoreMgr
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : BaseViewModel() {

    private val _shouldGoToNewList = MutableStateFlow(false)
    val shouldGoToNewList get() = _shouldGoToNewList
    private val _shouldRefreshList = MutableStateFlow(false)
    val shouldRefreshList get() = _shouldRefreshList

    fun insertTodoList(name: String) = flow {
        job = viewModelScope.launch {
            repository.insertTodoList(name)
            if (!getNotFirstTimeLaunch().first()) {
                setNotFirstTimeLaunch(true)
            }
        }
        emit(Resource.success(null))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.loading()
    )

    fun queryTodoList(name: String? = null) = repository.queryTodoList(name).asLiveData()

    fun updateTodoList(list: TodoList, name: String) {}
    fun deleteTodoList(list: TodoList) {}

    fun insertTodo(title: String, body: String?) {}
    fun queryTodo(name: String? = null) {}
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

    suspend fun getCurrentListId() = DataStoreMgr.getPreferences(DataStoreKeys.CURRENT_LIST, 1)
    suspend fun setCurrentListId(id: Int) =
        DataStoreMgr.setPreferences(DataStoreKeys.CURRENT_LIST, id)
}