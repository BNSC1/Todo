package com.bn.todo.data.repository

import com.bn.todo.data.db.TodoDatabase
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoList
import javax.inject.Inject

class TodoRepository @Inject constructor(database: TodoDatabase) {
    private val todoDao = database.todoDao()
    private val todoListDao = database.todoListDao()

    suspend fun insertTodoList(name: String) =
        todoListDao.insert(TodoList(name))

    fun queryTodoList(name: String? = null) = name?.let {
        todoListDao.query(it)
    } ?: todoListDao.query()

    suspend fun updateTodoList(list: TodoList, name: String) =
        todoListDao.update(list.copy(name = name))

    suspend fun deleteTodoList(list: TodoList) =
        todoListDao.delete(list)


    suspend fun insertTodo(title: String, body: String?, listId: Int) =
        todoDao.insert(Todo(title, body, listId))

    fun queryTodo(listId: Int, name: String? = null) = name?.let {
        todoDao.query(listId, it)
    } ?: todoDao.query(listId)

    suspend fun updateTodo(todo: Todo, title: String, body: String) =
        todoDao.update(todo.copy(title = title, body = body))

    suspend fun deleteTodo(todo: Todo) =
        todoDao.delete(todo)
}