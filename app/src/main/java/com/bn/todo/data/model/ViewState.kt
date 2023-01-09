package com.bn.todo.data.model

sealed class ViewState {
    object Loading : ViewState()
    object Idle : ViewState()
    object Success : ViewState()
}