package com.bn.todo.ui.welcome.viewmodel

import com.bn.todo.arch.BaseViewModel
import com.bn.todo.usecase.InsertListUseCase
import com.bn.todo.usecase.SetIsNotFirstLaunchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CreateListViewModel @Inject constructor(
    private val insertListUseCase: InsertListUseCase,
    private val setIsNotFirstLaunchUseCase: SetIsNotFirstLaunchUseCase
) : BaseViewModel() {
    private val _inputName = MutableStateFlow("")
    val inputName = _inputName.asStateFlow()

    fun insertTodoList(defaultName: String) = tryRun {
        insertListUseCase(inputName.value.ifEmpty { defaultName })
        setIsNotFirstLaunchUseCase()
    }

    fun setInputName(name: String) {
        _inputName.value = name
    }

}