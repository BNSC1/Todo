package com.bn.todo.arch

sealed class ViewModelMessage {
    data class Error(val msg: String): ViewModelMessage()

    sealed class Info: ViewModelMessage() {
        data class CompletedTodoDeletion(val count: Int): Info()
    }
}