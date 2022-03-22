package com.bn.todo.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.bn.todo.data.Resource
import com.bn.todo.data.State
import com.bn.todo.ktx.showDialog
import timber.log.Timber

abstract class ObserveStateFragment<Binding : ViewBinding> : BaseFragment<Binding>() {
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
        job = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.errorMsg.collect {
                if (it.isNotBlank()) {
                    showDialog(message = it)
                }
            }
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
                resource.message?.let { viewModel.errorMsg.tryEmit(it) }
            }
            State.LOADING -> loadingAction()
        }
    }
}
