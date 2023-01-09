package com.bn.todo.ui.welcome.viewmodel

import com.bn.todo.arch.BaseViewModel
import com.bn.todo.arch.EmitsViewState
import com.bn.todo.data.model.ViewState
import com.bn.todo.data.model.ViewState.Idle
import com.bn.todo.usecase.InsertTodoListUseCase
import com.bn.todo.usecase.SetCurrentListIdUseCase
import com.bn.todo.usecase.SetIsNotFirstLaunchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FirstTodoListViewModel @Inject constructor(
    private val insertTodoListUseCase: InsertTodoListUseCase,
    private val setIsNotFirstLaunchUseCase: SetIsNotFirstLaunchUseCase,
    private val setCurrentListIdUseCase: SetCurrentListIdUseCase
) : BaseViewModel(), EmitsViewState {
    private val _viewState = MutableStateFlow<ViewState>(Idle)
    override val viewState = _viewState.asStateFlow()
    private val _inputName = MutableStateFlow("")
    val inputName = _inputName.asStateFlow()

    fun insertTodoList(defaultName: String) = tryRun {
        setCurrentListIdUseCase(
            insertTodoListUseCase(inputName.value.ifEmpty { defaultName })
        )
        setIsNotFirstLaunchUseCase()
        _viewState.value = ViewState.Success
    }

    fun setInputName(name: String) {
        _inputName.value = name
    }

}