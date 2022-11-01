package com.bn.todo.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

abstract class ObserveStateBottomSheetDialogFragment<Binding : ViewBinding> :
    BaseBottomSheetDialogFragment<Binding>(), CollectsViewModelMessage {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViewBinding(container)
        collectMessage()
        return binding.root
    }
}
