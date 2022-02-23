package com.bn.todo.ui.view

import android.os.Bundle
import android.view.View
import com.bn.todo.R
import com.bn.todo.arch.BaseFragment
import com.bn.todo.databinding.FragmentCreateListBinding
import com.bn.todo.ui.viewmodel.TodoViewModel
import com.bn.todo.util.getTextOrDefault
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateListFragment : BaseFragment<FragmentCreateListBinding>() {
    @Inject
    lateinit var viewModel: TodoViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            nextBtn.setOnClickListener {
                val listName =
                    listNameInput.text.getTextOrDefault(getString(R.string.default_list_name))
                        .toString()
                viewModel.insertTodoList(listName)
            }
        }
    }
}