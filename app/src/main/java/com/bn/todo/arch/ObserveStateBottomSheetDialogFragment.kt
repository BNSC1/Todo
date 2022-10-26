package com.bn.todo.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.bn.todo.R
import com.bn.todo.ktx.collectLifecycleFlow
import com.bn.todo.ktx.showDialog
import com.bn.todo.ktx.showToast
import kotlinx.coroutines.launch

abstract class ObserveStateBottomSheetDialogFragment<Binding : ViewBinding> :
    BaseBottomSheetDialogFragment<Binding>() {
    abstract val viewModel: BaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViewBinding(container)
        collectMessage()
        return binding.root
    }

    private fun collectMessage() {
        job = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.message.collectLifecycleFlow(viewLifecycleOwner) {
                when (it) {
                    is ViewModelMessage.Error -> showDialog(message = it.msg)
                    is ViewModelMessage.Info.CompletedTodoDeletion ->
                        showToast(getString(R.string.msg_deleted_todos_format).format(it.count))
                }
            }
        }
    }
}
