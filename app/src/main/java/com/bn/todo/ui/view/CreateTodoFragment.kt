package com.bn.todo.ui.view

import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.bn.todo.R
import com.bn.todo.arch.ObserveStateFragment
import com.bn.todo.data.model.Todo
import com.bn.todo.databinding.FragmentCreateTodoBinding
import com.bn.todo.databinding.LayoutTextInputBinding
import com.bn.todo.ktx.collectFirstLifecycleFlow
import com.bn.todo.ui.MainActivity
import com.bn.todo.ui.viewmodel.TodoViewModel
import com.bn.todo.util.TextInputUtil
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateTodoFragment : ObserveStateFragment<FragmentCreateTodoBinding>() {
    @Inject
    override lateinit var viewModel: TodoViewModel
    private var isAllowed = false
    private val args by navArgs<CreateTodoFragmentArgs>()
    private val sourceFragment: String by lazy { args.sourceFragment }
    private var isEditMode = false
    private lateinit var clickedTodo: Todo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_create_todo, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.action_add) {
                    with(binding) {
                        val title = layoutTitleInput.input.text.toString()
                        val body = layoutBodyInput.input.text.toString()
                        if (isAllowed) {
                            if (isEditMode) {
                                viewModel.updateTodo(clickedTodo, title, body)
                                findNavController().popBackStack()
                            } else {
                                viewModel.insertTodo(title, body)
                                findNavController().popBackStack()
                            }
                        } else {
                            layoutTitleInput.setTitleInputError()
                            TextInputUtil.showKeyboard(requireActivity(), layoutTitleInput.input)
                        }
                    }
                }
                return NavigationUI.onNavDestinationSelected(
                    menuItem,
                    requireView().findNavController()
                )
            }
        }, viewLifecycleOwner, Lifecycle.State.STARTED)
        isEditMode = sourceFragment == TodoInfoFragment.TAG

        with(binding) {
            setupLayout()
            if (isEditMode) {
                (requireActivity() as MainActivity).supportActionBar?.title =
                    getString(R.string.title_edit_todo)
                fetchTodo()
            }
        }
    }

    private fun FragmentCreateTodoBinding.fetchTodo() {
        job = viewModel.clickedTodo.collectFirstLifecycleFlow(viewLifecycleOwner) {
            if (it != null) {
                clickedTodo = it
            }
        }
        layoutTitleInput.input.setText(clickedTodo.title)
        layoutBodyInput.input.setText(clickedTodo.body)
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
                    isAllowed = !target.text.isNullOrBlank()
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