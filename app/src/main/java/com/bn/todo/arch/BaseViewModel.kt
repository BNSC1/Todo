package com.bn.todo.arch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    protected var job: Job? = null
    protected val _errorMsg = MutableSharedFlow<String>(replay = 0)
    val errorMsg = _errorMsg.asSharedFlow()

    protected fun tryRun(
        scope: CoroutineScope = viewModelScope,
        failureAction: suspend (Throwable) -> Unit = {},
        action: suspend () -> Unit
    ) = scope.launch {
        runCatching {
            action()
        }.onFailure {
            _errorMsg.emit(it.message.toString())
            failureAction(it)
        }
    }
}
