package com.bn.todo.ui.view

import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bn.todo.R
import com.bn.todo.arch.ObserveStateFragment
import com.bn.todo.databinding.FragmentCreateTodoBinding
import com.bn.todo.databinding.LayoutTextInputBinding
import com.bn.todo.ui.viewmodel.TodoViewModel
import com.bn.todo.util.TextInputUtil
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateTodoFragment : ObserveStateFragment<FragmentCreateTodoBinding>() {
    private var isCreationAllowed = false

    @Inject
    override lateinit var viewModel: TodoViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        with(binding) {
            setupLayout()

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_create_todo, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            with(binding) {
                val title = layoutTitleInput.input.text.toString()
                val body = layoutBodyInput.input.text.toString()
                if (isCreationAllowed) {
                    job = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        viewModel.insertTodo(title, body).collect {
                            handleState(it, {
                                findNavController().popBackStack()
                            })
                        }
                    }
                } else {
                    layoutTitleInput.setTitleInputError()
                    TextInputUtil.showKeyboard(requireActivity(), layoutTitleInput.input)
                }
            }
        }
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        ) || super.onOptionsItemSelected(item)
    }

    private fun FragmentCreateTodoBinding.setupLayout() {
        layoutTitleInput.apply {
            root.hint = getString(R.string.todo_title)
            root.isErrorEnabled = true
            TextInputUtil.showKeyboard(requireActivity(), input)
            input.addTextChangedListener(object :
                TextInputUtil.TextChangedListener<TextInputEditText>(input) {
                override fun onTextChanged(target: TextInputEditText, s: Editable?) {
                    root.error = ""
                    isCreationAllowed = !target.text.isNullOrBlank()
                }
            })
        }
        layoutBodyInput.apply {
            root.hint = getString(R.string.todo_body)
            input.isSingleLine = false
            input.minLines = 2
        }
    }

    private fun LayoutTextInputBinding.setTitleInputError() {
        root.error = getString(R.string.error_title_required)
    }
}