package com.bn.todo.ui.view

import android.os.Bundle
import android.view.View
import com.bn.todo.R
import com.bn.todo.arch.ObserveStateFragment
import com.bn.todo.databinding.FragmentCreateListBinding
import com.bn.todo.ktx.getTextOrDefault
import com.bn.todo.ui.viewmodel.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateListFragment : ObserveStateFragment<FragmentCreateListBinding>() {
    @Inject
    override lateinit var viewModel: TodoViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            nextBtn.setOnClickListener {
                val listName =
                    listNameInput.text.getTextOrDefault(getString(R.string.default_list_name))
                        .toString()
                insertTodoList(listName)
            }
        }
    }

    private fun insertTodoList(listName: String) {
        viewModel.insertTodoList(listName).observe(viewLifecycleOwner) {
            handleState(it, {
                CreateListFragmentDirections.actionToMainActivity().navigate()
                requireActivity().finish()
            })
        }
    }
}