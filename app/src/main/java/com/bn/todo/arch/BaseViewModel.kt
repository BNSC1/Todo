package com.bn.todo.arch

import androidx.lifecycle.ViewModel

abstract class BaseViewModel(
    initialState: State = State.LOADING
) : ViewModel() {

    enum class State {
        LOADED, LOADING, LOADING_FAILED
    }

}
