package com.bn.todo.ui.main.viewmodel

import com.bn.todo.arch.BaseViewModel
import com.bn.todo.arch.HasListAction
import com.bn.todo.arch.ViewModelMessage
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoList
import com.bn.todo.usecase.DeleteCompletedTodosUseCase
import com.bn.todo.usecase.DeleteTodoUseCase
import com.bn.todo.usecase.InsertTodoUseCase
import com.bn.todo.usecase.UpdateTodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TodoOperationViewModel @Inject constructor(
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteCompletedTodosUseCase: DeleteCompletedTodosUseCase,
    private val insertTodoUseCase: InsertTodoUseCase
) : BaseViewModel(), HasListAction {
    private val _clickedTodo = MutableStateFlow<Todo?>(null)
    val clickedTodo = _clickedTodo.asStateFlow()

    fun setClickedTodo(clickedTodo: Todo) {
        _clickedTodo.value = clickedTodo
    }

    fun insertTodo(currentList: TodoList?, title: String, body: String? = null) = tryRun {
        tryListAction(currentList) {
            insertTodoUseCase(
                it,
                title,
                body
            )
        }
    }

    fun updateTodo(todo: Todo, name: String, body: String?) = tryRun {
        updateTodoUseCase(todo, name, body)
    }

    fun updateTodo(todo: Todo, isCompleted: Boolean) = tryRun {
        updateTodoUseCase(todo, isCompleted)
    }

    fun deleteTodo(todo: Todo) = tryRun {
        deleteTodoUseCase(todo)
    }

    fun deleteCompletedTodos(currentList: TodoList?) = tryRun {
        val deletedTodoCount = tryListAction(currentList) {
            deleteCompletedTodosUseCase(it)
        }
        _message.emit(deletedTodoCount.let {
            ViewModelMessage.Info.CompletedTodoDeletion(it as Int)
        }
        )
    }
}