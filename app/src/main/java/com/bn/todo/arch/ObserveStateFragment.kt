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
import com.bn.todo.ktx.collectLatestLifecycleFlow
import com.bn.todo.ktx.showToast
import kotlinx.coroutines.launch
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
        viewModel.errorMsg.collectLatestLifecycleFlow(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                showToast(message = it, Toast.LENGTH_LONG)
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
                job = viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.errorMsg.emit(
                        resource.message ?: resource.messageResId?.let { getString(it) }
                        ?: getString(R.string.err_unknown)
                    )
                }
            }
            State.LOADING -> loadingAction()
        }
    }
}
