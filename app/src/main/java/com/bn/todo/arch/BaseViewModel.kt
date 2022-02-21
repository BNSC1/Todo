package com.bn.todo.arch

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

abstract class BaseViewModel : ViewModel() {
    private var job: Job? = null
}
