package com.bn.todo.ui.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bn.todo.R
import com.bn.todo.arch.ObserveStateFragment
import com.bn.todo.databinding.FragmentCreateListBinding
import com.bn.todo.ktx.getTextOrDefault
import com.bn.todo.ui.viewmodel.TodoViewModel
import com.bn.todo.util.TextInputUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateListFragment : ObserveStateFragment<FragmentCreateListBinding>() {
    @Inject
    override lateinit var viewModel: TodoViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            listNameInputLayout.root.hint = getString(R.string.list_name)
            TextInputUtil.showKeyboard(requireActivity(), listNameInputLayout.input)
            nextBtn.setOnClickListener {
                val listName =
                    listNameInputLayout.input.text.getTextOrDefault(getString(R.string.default_list_name))
                        .toString()
                insertTodoList(listName)
            }
        }
    }

    private fun insertTodoList(listName: String) {
        job = lifecycleScope.launch {
            viewModel.insertTodoList(listName).collect { res ->
                handleState(res, {
                    CreateListFragmentDirections.actionToMainActivity().navigate()
                    requireActivity().finish()
                })
            }
        }
    }
}