package com.bn.todo.ui.viewmodel

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.bn.todo.arch.BaseViewModel
import com.bn.todo.data.Resource
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoList
import com.bn.todo.data.repository.TodoRepository
import com.bn.todo.util.DataStoreKeys
import com.bn.todo.util.DataStoreMgr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : BaseViewModel() {

    fun insertTodoList(name: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading())
        job = viewModelScope.launch {
            repository.insertTodoList(name)
            setNotFirstTimeLaunch(true)
        }
        emit(Resource.success(null))
    }

    fun queryTodoList(name: String? = null) {}
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
}