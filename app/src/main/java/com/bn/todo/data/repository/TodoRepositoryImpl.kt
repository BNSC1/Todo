package com.bn.todo.data.repository

import com.bn.todo.data.dao.TodoDao
import com.bn.todo.data.dao.TodoListDao
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoFilter
import com.bn.todo.data.model.TodoList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepositoryImpl @Inject constructor(
    private val todoDao: TodoDao,
    private val todoListDao: TodoListDao
) : TodoRepository {
    override suspend fun insertTodoList(name: String) = todoListDao.insert(TodoList(name))

    override fun queryTodoList(name: String?) = name?.let {
        todoListDao.query(it)
    } ?: todoListDao.query()

    override suspend fun updateTodoList(list: TodoList, name: String) =
        todoListDao.update(list.copy(name = name))

    override suspend fun deleteTodoList(list: TodoList) = todoListDao.delete(list)


    override suspend fun insertTodo(title: String, body: String?, listId: Long) =
        todoDao.insert(Todo(title, body, listId))

    override fun queryTodoFlow(todoFilter: TodoFilter): Flow<List<Todo>> {
        todoFilter.apply {
            return if (showCompleted) {
                todoDao.query(todoFilter.listId, name)
            } else {
                todoDao.queryNotCompleted(todoFilter.listId)
            }
        }
    }

    override suspend fun updateTodo(todo: Todo, title: String, body: String?) =
        todoDao.update(todo.copy(title = title, body = body))

    override suspend fun updateTodo(todo: Todo, isCompleted: Boolean) =
        todoDao.update(todo.copy(isCompleted = isCompleted))

    override suspend fun deleteTodo(todo: Todo) = todoDao.delete(todo)

    override suspend fun deleteCompletedTodo(listId: Long) = todoDao.deleteCompleted(listId)
}