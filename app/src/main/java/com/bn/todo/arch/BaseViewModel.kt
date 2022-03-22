package com.bn.todo.arch

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow

abstract class BaseViewModel : ViewModel() {
    protected var job: Job? = null
    private val _errorMsg: MutableSharedFlow<String> by lazy { MutableSharedFlow<String>() }
    val errorMsg get() = _errorMsg

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        errorMsg.tryEmit(throwable.toString())
    }
}
