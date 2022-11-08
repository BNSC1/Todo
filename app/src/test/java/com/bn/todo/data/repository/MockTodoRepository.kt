package com.bn.todo.data.repository

import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoFilter
import com.bn.todo.data.model.TodoList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockTodoRepository : TodoRepository {
    val todoLists = mutableListOf<TodoList>()

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

    override suspend fun insertTodo(title: String, body: String?, listId: Long) {
        TODO("Not yet implemented")
    }

    override fun queryTodoFlow(todoFilter: TodoFilter): Flow<List<Todo>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateTodo(todo: Todo, title: String, body: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTodo(todo: Todo, isCompleted: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTodo(todo: Todo) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCompletedTodo(listId: Long): Int {
        TODO("Not yet implemented")
    }
}