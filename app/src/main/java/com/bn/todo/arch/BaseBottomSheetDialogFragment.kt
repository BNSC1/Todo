package com.bn.todo.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment<Binding : ViewBinding> : BottomSheetDialogFragment(),
    InitViewBinding<Binding> {
    override var _binding: Binding? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViewBinding(layoutInflater, container)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        removeViewBinding()
    }
}