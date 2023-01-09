package com.bn.todo.arch

import com.bn.todo.data.model.ViewState
import kotlinx.coroutines.flow.Flow

interface EmitsViewState {
    val viewState: Flow<ViewState>
}