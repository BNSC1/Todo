package com.bn.todo.ui.welcome.view

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.bn.todo.R
import com.bn.todo.arch.BaseFragment
import com.bn.todo.arch.CollectsViewModelMessage
import com.bn.todo.arch.CollectsViewState
import com.bn.todo.arch.HidesActionBar
import com.bn.todo.data.model.ViewState
import com.bn.todo.databinding.FragmentFirstTodoListBinding
import com.bn.todo.ui.welcome.viewmodel.FirstTodoListViewModel
import com.bn.todo.util.TextInputUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirstTodoListFragment : BaseFragment<FragmentFirstTodoListBinding>(),
    CollectsViewModelMessage, CollectsViewState, HidesActionBar {
    override val viewModel: FirstTodoListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectMessage(viewModel)
        collectViewState()

        with(binding) {
            setupInput()
            setupNextButton()
        }
    }

    override fun onStart() {
        super.onStart()
        hideActionBar()
    }

    override fun onStop() {
        super.onStop()
        showActionBar()
    }

    override fun onViewStateChanged(viewState: ViewState) {
        if (viewState is ViewState.Success) {
            goToNextPage()
        }
    }

    private fun goToNextPage() {
        FirstTodoListFragmentDirections.actionToListFragment().navigate()
    }

    private fun FragmentFirstTodoListBinding.setupNextButton() {
        nextBtn.setOnClickListener {
            insertTodoList()
        }
    }

    private fun FragmentFirstTodoListBinding.setupInput() {
        listNameInputLayout.root.hint = getString(R.string.list_name)
        TextInputUtil.showKeyboard(requireActivity(), listNameInputLayout.input)
        listNameInputLayout.input.doOnTextChanged { text, _, _, _ ->
            viewModel.setInputName(text.toString())
        }
    }

    private fun insertTodoList() {
        viewModel.insertTodoList(getString(R.string.default_list_name))
    }
}