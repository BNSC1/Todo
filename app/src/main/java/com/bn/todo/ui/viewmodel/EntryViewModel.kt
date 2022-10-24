package com.bn.todo.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.bn.todo.arch.BaseViewModel
import com.bn.todo.data.repository.UserPrefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val userPrefRepository: UserPrefRepository
) : BaseViewModel() {
    private var _isFirstLaunch: SharedFlow<Boolean>
    val isFirstLaunch get() = _isFirstLaunch

    init {
        _isFirstLaunch = getIsFirstLaunchFlow()
    }

    private fun getIsFirstLaunchFlow() =
        userPrefRepository.getIsFirstTimeLaunch().shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed()
        )
}