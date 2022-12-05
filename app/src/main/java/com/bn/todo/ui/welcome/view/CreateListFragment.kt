package com.bn.todo.ui.welcome.view

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.bn.todo.R
import com.bn.todo.arch.BaseFragment
import com.bn.todo.arch.CollectsViewModelMessage
import com.bn.todo.databinding.FragmentCreateListBinding
import com.bn.todo.ui.welcome.viewmodel.CreateListViewModel
import com.bn.todo.util.TextInputUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateListFragment : BaseFragment<FragmentCreateListBinding>(), CollectsViewModelMessage {
    override val viewModel: CreateListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectMessage()

        with(binding) {
            setupInput()
            setupNextButton()
        }
    }

    private fun FragmentCreateListBinding.setupNextButton() {
        nextBtn.setOnClickListener {
            insertTodoList()
            CreateListFragmentDirections.actionToMainActivity().navigate()
            requireActivity().finish()
        }
    }

    private fun FragmentCreateListBinding.setupInput() {
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