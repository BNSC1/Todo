package com.bn.todo.ui.callback

import com.bn.todo.data.model.Todo

interface TodoClickCallback {
    fun onClick(todo: Todo)
}