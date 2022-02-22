package com.bn.todo.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.bn.todo.arch.BaseViewModel
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoList
import com.bn.todo.util.DataStoreKeys
import com.bn.todo.util.DataStoreMgr
import kotlinx.coroutines.launch
import javax.inject.Inject

class TodoViewModel @Inject constructor() : BaseViewModel() {

    fun createTodoList(name: String) {
        job = viewModelScope.launch {
            setNotFirstTimeLaunch(true)
        }
    }

    fun queryTodoList(name: String? = null) {}
    fun editTodoList(list: TodoList, name: String) {}
    fun deleteTodoList(list: TodoList) {}

    fun createTodo(title: String, body: String?) {}
    fun queryTodo(name: String? = null) {}
    fun editTodo(todo: Todo, name: String? = null, body: String?) {}
    fun deleteTodo(todo: Todo) {}

    private suspend fun setNotFirstTimeLaunch(isNotFirstTimeLaunch: Boolean) =
        DataStoreMgr.savePreferences(DataStoreKeys.NOT_FIRST_LAUNCH, isNotFirstTimeLaunch)
}