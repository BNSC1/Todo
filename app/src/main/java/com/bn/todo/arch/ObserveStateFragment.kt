package com.bn.todo.arch

import androidx.viewbinding.ViewBinding
import com.bn.todo.data.Resource
import com.bn.todo.data.State
import com.bn.todo.ktx.showDialog
import timber.log.Timber

abstract class ObserveStateFragment<Binding : ViewBinding> : BaseFragment<Binding>() {
    abstract val viewModel: BaseViewModel
    fun observeErrorMsg() {
        viewModel.errorMsg.observe(viewLifecycleOwner) {
            showDialog(message = it)
        }
    }


    fun handleState(
        resource: Resource<*>,
        successAction: () -> Unit,
        errorAction: () -> Unit = {},
        loadingAction: () -> Unit = {}
    ) {
        Timber.d("state is ${resource.state}")
        when (resource.state) {
            State.SUCCESS -> successAction()
            State.ERROR -> {
                errorAction()
                resource.message?.let { viewModel.setErrorMsg(it) }
            }
            State.LOADING -> loadingAction()
        }
    }
}
