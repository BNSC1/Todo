package com.bn.todo.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.bn.todo.R
import com.bn.todo.ktx.collectLatestLifecycleFlow
import com.bn.todo.ktx.showDialog
import com.bn.todo.ktx.showToast

abstract class ObserveStateFragment<Binding : ViewBinding> : BaseFragment<Binding>() {
    protected abstract val viewModel: BaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViewBinding(container)
        collectMessage()
        return binding.root
    }

    private fun collectMessage() {
        viewModel.message.collectLatestLifecycleFlow(viewLifecycleOwner) { msg ->
            when (msg) {
                is ViewModelMessage.Error -> {
                    showDialog(message = msg.msg ?:
                    msg.msgStringId?.let { getString(it) } ?:
                    getString(R.string.err_unknown)
                    )
                }
                is ViewModelMessage.Info.CompletedTodoDeletion ->
                    showToast(getString(R.string.msg_deleted_todos_format).format(msg.count))
            }
        }
    }
}
