package com.bn.todo.data.repository

import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoFilter
import com.bn.todo.data.model.TodoList
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    suspend fun insertTodoList(name: String): Long
    fun queryTodoList(name: String? = null): Flow<List<TodoList>>
    suspend fun updateTodoList(list: TodoList, name: String)
    suspend fun deleteTodoList(list: TodoList)
    suspend fun insertTodo(title: String, body: String?, listId: Long)
    fun queryTodoFlow(todoFilter: TodoFilter): Flow<List<Todo>>
    suspend fun updateTodo(todo: Todo, title: String, body: String)
    suspend fun updateTodo(todo: Todo, isCompleted: Boolean)
    suspend fun deleteTodo(todo: Todo)
    suspend fun deleteCompletedTodo(listId: Long): Int
}