package com.bn.todo.ui.main.view

import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.bn.todo.R
import com.bn.todo.arch.BaseFragment
import com.bn.todo.arch.CollectsViewModelMessage
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoList
import com.bn.todo.databinding.FragmentTodoOperationBinding
import com.bn.todo.databinding.LayoutTextInputBinding
import com.bn.todo.ktx.collectLatestLifecycleFlow
import com.bn.todo.ui.MainActivity
import com.bn.todo.ui.main.viewmodel.TodoListViewModel
import com.bn.todo.ui.main.viewmodel.TodoOperationViewModel
import com.bn.todo.util.TextInputUtil
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoOperationFragment : BaseFragment<FragmentTodoOperationBinding>(),
    CollectsViewModelMessage {
    override val viewModel: TodoOperationViewModel by viewModels()
    private val listViewModel: TodoListViewModel by viewModels()
    private var isAllowed = false
    private val args by navArgs<TodoOperationFragmentArgs>()
    private lateinit var sourceFragment: String
    private lateinit var strategy: TodoStrategy

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectMessage()
        setupMenu()

        with(binding) {
            setupLayout()
            collectCurrentList {
                setupStrategy(it)
                strategy.apply {
                    setupAction()
                }
            }
        }
    }

    private fun collectCurrentList(onCollectAction: (TodoList?) -> Unit) {
        listViewModel.currentList.collectLatestLifecycleFlow(viewLifecycleOwner) {
            onCollectAction(it)
        }
    }

    private fun setupStrategy(currentList: TodoList?) {
        sourceFragment = args.sourceFragment
        strategy =
            if (sourceFragment == TodoInfoFragment::class.java.name) TodoStrategy.EditStrategy(args.clickedTodo)
            else TodoStrategy.AddStrategy(currentList)
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_create_todo, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.action_add) {
                    binding.setupAddAction()
                }
                return NavigationUI.onNavDestinationSelected(
                    menuItem,
                    requireView().findNavController()
                )
            }
        }, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    private fun FragmentTodoOperationBinding.setupAddAction() {
        val title = layoutTitleInput.input.text.toString()
        val body = layoutBodyInput.input.text.toString()
        if (isAllowed) {
            strategy.apply {
                viewModel.finishAction(title, body)
            }
            findNavController().popBackStack()
        } else {
            layoutTitleInput.setTitleInputError()
            TextInputUtil.showKeyboard(requireActivity(), layoutTitleInput.input)
        }
    }

    private fun FragmentTodoOperationBinding.setupLayout() {
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

    sealed class TodoStrategy {
        abstract fun TodoOperationFragment.setupAction()
        abstract fun TodoOperationViewModel.finishAction(
            title: String,
            body: String
        )

        class EditStrategy(private val clickedTodo: Todo?) : TodoStrategy() {
            override fun TodoOperationFragment.setupAction() {
                (requireActivity() as MainActivity).supportActionBar?.title =
                    getString(R.string.title_edit_todo)

                with(binding) {
                    layoutTitleInput.input.setText(clickedTodo?.title)
                    layoutBodyInput.input.setText(clickedTodo?.body)
                }
            }

            override fun TodoOperationViewModel.finishAction(
                title: String,
                body: String
            ) {
                this@EditStrategy.clickedTodo?.let {
                    updateTodo(it, title, body)
                }
            }

        }

        class AddStrategy(private val currentList: TodoList?) : TodoStrategy() {
            override fun TodoOperationFragment.setupAction() {}

            override fun TodoOperationViewModel.finishAction(
                title: String,
                body: String
            ) {
                insertTodo(currentList, title, body)
            }

        }
    }
}