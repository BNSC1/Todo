package com.bn.todo.data.repository

import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoFilter
import com.bn.todo.data.model.TodoList
import kotlinx.coroutines.flow.flow
import java.time.OffsetDateTime

class MockTodoRepository : TodoRepository {
    val todoLists = mutableListOf<TodoList>()
    val todos = mutableListOf<Todo>()

    override suspend fun insertTodoList(name: String) =
        (todoLists.size.toLong() + 1).also {
            todoLists.add(TodoList(name, it))
        }

    override fun queryTodoList(name: String?) = flow {
        emit(todoLists)
    }

    override suspend fun updateTodoList(list: TodoList, name: String) {
        todoLists.apply {
            set(
                indexOfFirst { it == list },
                list.copy(name = name)
            )
        }
    }

    override suspend fun deleteTodoList(list: TodoList) {
        todoLists.remove(list)
    }

    override suspend fun insertTodo(
        title: String,
        body: String?,
        listId: Long,
        createdTime: OffsetDateTime
    ) {
        (todos.size.toLong() + 1).also {
            todos.add(Todo(title, body, listId, id = it, createdTime = createdTime))
        }
    }

    override fun queryTodoFlow(todoFilter: TodoFilter) = flow {
        emit(todos.filter {
            todoFilter.listId == it.listId
        })
    }

    override suspend fun updateTodo(todo: Todo, title: String, body: String?) {
        todos.apply {
            set(
                indexOfFirst { it == todo },
                todo.copy(title = title, body = body)
            )
        }
    }

    override suspend fun updateTodo(todo: Todo, isCompleted: Boolean) {
        todos.apply {
            set(
                indexOfFirst { it == todo },
                todo.copy(isCompleted = isCompleted)
            )
        }
    }

    override suspend fun deleteTodo(todo: Todo) {
        todos.remove(todo)
    }

    override suspend fun deleteCompletedTodo(listId: Long): Int {
        TODO("Not yet implemented")
    }
}