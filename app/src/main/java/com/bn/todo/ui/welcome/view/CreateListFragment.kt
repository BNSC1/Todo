package com.bn.todo.ui.welcome.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.bn.todo.R
import com.bn.todo.arch.BaseFragment
import com.bn.todo.arch.CollectsViewModelMessage
import com.bn.todo.databinding.FragmentCreateListBinding
import com.bn.todo.ktx.getTextOrDefault
import com.bn.todo.ui.viewmodel.TodoViewModel
import com.bn.todo.util.TextInputUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateListFragment : BaseFragment<FragmentCreateListBinding>(), CollectsViewModelMessage {
    override val viewModel: TodoViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectMessage()

        with(binding) {
            listNameInputLayout.root.hint = getString(R.string.list_name)
            TextInputUtil.showKeyboard(requireActivity(), listNameInputLayout.input)
            nextBtn.setOnClickListener {
                val listName =
                    listNameInputLayout.input.text.getTextOrDefault(getString(R.string.default_list_name))
                        .toString()
                insertTodoList(listName)
                CreateListFragmentDirections.actionToMainActivity().navigate()
                requireActivity().finish()
            }
        }
    }

    private fun insertTodoList(listName: String) {
        viewModel.insertTodoList(listName)
    }
}