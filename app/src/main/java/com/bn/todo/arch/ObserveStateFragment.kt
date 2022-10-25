package com.bn.todo.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.bn.todo.R
import com.bn.todo.data.Resource
import com.bn.todo.data.State
import com.bn.todo.ktx.collectLatestLifecycleFlow
import com.bn.todo.ktx.showDialog
import com.bn.todo.ktx.showToast
import timber.log.Timber

abstract class ObserveStateFragment<Binding : ViewBinding> : BaseFragment<Binding>() {
    protected abstract val viewModel: BaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViewBinding(container)
        collectErrorMessage()
        return binding.root
    }

    private fun collectErrorMessage() {
        viewModel.message.collectLatestLifecycleFlow(viewLifecycleOwner) {
            when (it) {
                is ViewModelMessage.Error -> showDialog(message = it.msg)
                is ViewModelMessage.Info.CompletedTodoDeletion ->
                    showToast(getString(R.string.msg_deleted_todos_format).format(it.count))
            }
        }
    }


    fun handleState(
        resource: Resource<*>,
        errorAction: () -> Unit = {},
        loadingAction: () -> Unit = {},
        successAction: () -> Unit
    ) {
        Timber.d("state is ${resource.state}")
        when (resource.state) {
            State.SUCCESS -> successAction()
            State.ERROR -> {
                errorAction()
            }
            State.LOADING -> loadingAction()
        }
    }
}
