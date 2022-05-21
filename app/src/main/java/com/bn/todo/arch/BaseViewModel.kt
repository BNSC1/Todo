package com.bn.todo.arch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bn.todo.data.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlin.experimental.ExperimentalTypeInference

abstract class BaseViewModel : ViewModel() {
    protected var job: Job? = null
    private val _errorMsg: MutableSharedFlow<String> by lazy { MutableSharedFlow() }
    val errorMsg get() = _errorMsg

    @OptIn(ExperimentalTypeInference::class)
    inline fun <T> ViewModel.stateFlow(
        scope: CoroutineScope = viewModelScope,
        started: SharingStarted = SharingStarted.Lazily,
        initValue: Resource<T> = Resource.loading(),
        @BuilderInference crossinline action: suspend FlowCollector<Resource<T>>.() -> Unit,
    ): StateFlow<Resource<T>> =
        flow {
            action()
        }.stateIn(
            scope = scope,
            started = started,
            initialValue = initValue
        )
}
