package com.bn.todo.ktx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

inline fun <T> Flow<T>.collectLifecycleFlow(
    lifecycleOwner: LifecycleOwner,
    minState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline collect: suspend CoroutineScope.(T) -> Unit
) =
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(minState) {
            collect {
                collect(it)
            }
        }
    }

inline fun <T> Flow<T>.collectLatestLifecycleFlow(
    lifecycleOwner: LifecycleOwner,
    minState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline collect: suspend CoroutineScope.(T) -> Unit
) =
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(minState) {
            collectLatest {
                collect(it)
            }
        }
    }

inline fun <T> Flow<T>.collectFirstLifecycleFlow(
    lifecycleOwner: LifecycleOwner,
    crossinline collect: suspend CoroutineScope.(T) -> Unit
) =
    lifecycleOwner.lifecycleScope.launch {
        collect(first())
    }