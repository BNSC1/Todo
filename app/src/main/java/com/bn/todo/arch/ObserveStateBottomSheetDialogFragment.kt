package com.bn.todo.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.bn.todo.R
import com.bn.todo.data.Resource
import com.bn.todo.data.State
import com.bn.todo.ktx.collectLifecycleFlow
import com.bn.todo.ktx.showDialog
import com.bn.todo.ktx.showToast
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class ObserveStateBottomSheetDialogFragment<Binding : ViewBinding> :
    BaseBottomSheetDialogFragment<Binding>() {
    abstract val viewModel: BaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViewBinding(container)
        observeErrorMsg()
        return binding.root
    }

    private fun observeErrorMsg() {
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


    fun handleState(
        resource: Resource<*>,
        successAction: () -> Unit,
        errorAction: () -> Unit = {},
        loadingAction: () -> Unit = {},
    ) {
        Timber.d("state is ${resource.state}")
        when (resource.state) {
            State.SUCCESS -> successAction()
            State.ERROR -> {
                errorAction()
                showToast(resource.message ?: resource.messageResId?.let { getString(it) }
                ?: getString(R.string.err_unknown), Toast.LENGTH_LONG)
            }
            State.LOADING -> loadingAction()
        }
    }
}
