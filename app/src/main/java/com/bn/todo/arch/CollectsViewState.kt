package com.bn.todo.arch

import androidx.fragment.app.Fragment
import com.bn.todo.data.model.ViewState
import com.bn.todo.ktx.collectLatestLifecycleFlow

interface CollectsViewState {
    val viewModel: EmitsViewState

    fun Fragment.collectViewState() {
        viewModel.viewState.collectLatestLifecycleFlow(viewLifecycleOwner) {
            onViewStateChanged(it)
        }
    }

    fun onViewStateChanged(viewState: ViewState)
}