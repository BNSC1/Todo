package com.bn.todo.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bn.todo.arch.BaseViewModel
import com.bn.todo.data.Resource
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoList
import com.bn.todo.data.repository.TodoRepository
import com.bn.todo.util.DataStoreKeys
import com.bn.todo.util.DataStoreMgr
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : BaseViewModel() {

    val shouldGoToNewList = MutableLiveData<Boolean>(false)
    val testShouldGoToNewList = MutableSharedFlow<Boolean>()
    val shouldRefreshList = MutableLiveData<Boolean>(false)

    fun insertTodoList(name: String) = flow {
        job = viewModelScope.launch {
            repository.insertTodoList(name)
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
        DataStoreMgr.savePreferences(DataStoreKeys.NOT_FIRST_LAUNCH, isNotFirstTimeLaunch)

    suspend fun loadCurrentListId() = DataStoreMgr.readPreferences(DataStoreKeys.CURRENT_LIST, 1)
    suspend fun saveCurrentListId(id: Int) =
        DataStoreMgr.savePreferences(DataStoreKeys.CURRENT_LIST, id)
}