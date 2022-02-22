package com.bn.todo.arch

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

abstract class BaseViewModel : ViewModel() {
    protected var job: Job? = null

    override fun onCleared() {
        super.onCleared()
        job?.apply {
            cancel()
            job = null
        }
    }
}
