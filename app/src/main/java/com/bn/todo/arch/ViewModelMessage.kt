package com.bn.todo.arch

sealed class ViewModelMessage {
    data class Error(val msg: String? = null, val msgStringId: Int? = null): ViewModelMessage()

    sealed class Info: ViewModelMessage() {
        data class CompletedTodoDeletion(val count: Int): Info()
    }
}