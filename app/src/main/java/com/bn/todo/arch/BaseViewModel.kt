package com.bn.todo.arch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bn.todo.data.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.experimental.ExperimentalTypeInference

abstract class BaseViewModel : ViewModel() {
    protected var job: Job? = null
    protected val _errorMsg = MutableStateFlow("")
    val errorMsg = _errorMsg.asStateFlow()

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

    protected fun tryLaunchAction(
        scope: CoroutineScope = viewModelScope,
        failureAction: suspend (Throwable) -> Unit = {},
        action: suspend () -> Unit
    ) = scope.launch {
        runCatching {
            action()
        }.onFailure {
            _errorMsg.value = it.message.toString()
            failureAction(it)
        }
    }
}
