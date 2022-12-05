package com.bn.todo.ui.viewmodel

import com.bn.todo.arch.BaseViewModel
import com.bn.todo.data.model.TodoList
import com.bn.todo.usecase.UpdateListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val updateListUseCase: UpdateListUseCase,
) : BaseViewModel() {
    fun updateTodoList(list: TodoList, name: String) = tryRun {
        updateListUseCase(list, name)
    }
}