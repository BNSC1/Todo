package com.bn.todo.data.repository

import com.bn.todo.data.dao.TodoDao
import com.bn.todo.data.dao.TodoListDao
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoFilter
import com.bn.todo.data.model.TodoList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(
    private val todoDao: TodoDao,
    private val todoListDao: TodoListDao
) {

    suspend fun insertTodoList(name: String) = withContext(Dispatchers.IO) {
        todoListDao.insert(TodoList(name))
    }

    fun queryTodoList(name: String? = null) = name?.let {
        todoListDao.query(it)
    } ?: todoListDao.query()

    suspend fun updateTodoList(list: TodoList, name: String) = withContext(Dispatchers.IO) {
        todoListDao.update(list.copy(name = name))
    }

    suspend fun deleteTodoList(list: TodoList) = withContext(Dispatchers.IO) {
        todoListDao.delete(list)
    }


    suspend fun insertTodo(title: String, body: String?, listId: Int) =
        withContext(Dispatchers.IO) {
            todoDao.insert(Todo(title, body, listId))
        }

    fun queryTodo(todoFilter: TodoFilter, sortField: Int): Flow<List<Todo>> {
        todoFilter.apply {
            return if (showCompleted) {
                todoDao.query(todoFilter.listId, name, sortField)
            } else {
                todoDao.queryNotCompleted(todoFilter.listId)
            }
        }
    }

    suspend fun updateTodo(todo: Todo, title: String, body: String) = withContext(Dispatchers.IO) {
        todoDao.update(todo.copy(title = title, body = body))
    }

    suspend fun updateTodo(todo: Todo, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        todoDao.update(todo.copy(isCompleted = isCompleted))
    }

    suspend fun deleteTodo(todo: Todo) = withContext(Dispatchers.IO) {
        todoDao.delete(todo)
    }

    suspend fun deleteCompletedTodo(listId: Int) = withContext(Dispatchers.IO) {
        todoDao.deleteCompleted(listId)
    }
}