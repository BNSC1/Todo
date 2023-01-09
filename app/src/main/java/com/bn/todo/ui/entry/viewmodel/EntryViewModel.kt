package com.bn.todo.ui.entry.viewmodel

import androidx.lifecycle.viewModelScope
import com.bn.todo.arch.BaseViewModel
import com.bn.todo.usecase.GetIsFirstLaunchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val getIsFirstLaunchUseCase: GetIsFirstLaunchUseCase
) : BaseViewModel() {
    private var _isFirstLaunch: SharedFlow<Boolean>
    val isFirstLaunch get() = _isFirstLaunch

    init {
        _isFirstLaunch = getIsFirstLaunchFlow()
    }

    private fun getIsFirstLaunchFlow() =
        getIsFirstLaunchUseCase().shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed()
        )
}