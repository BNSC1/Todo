package com.bn.todo.ui.viewmodel

import com.bn.todo.arch.BaseViewModel
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoList

class TodoViewModel : BaseViewModel() {

    fun createList(name: String) {}
    fun queryList(name: String? = null) {}
    fun editList(list: TodoList, name: String) {}
    fun deleteList(list: TodoList) {}

    fun createTodo(title: String, body: String?) {}
    fun queryTodo(name: String? = null) {}
    fun editTodo(todo: Todo, name: String? = null, body: String?) {}
    fun deleteTodo(todo: Todo) {}
}