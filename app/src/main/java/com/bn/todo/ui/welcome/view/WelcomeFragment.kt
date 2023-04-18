package com.bn.todo.ui.welcome.view

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.bn.todo.R
import com.bn.todo.arch.BaseFragment
import com.bn.todo.arch.HidesActionBar
import com.bn.todo.databinding.FragmentWelcomeBinding

class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>(), HidesActionBar {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            setupLayout()
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

    private fun FragmentWelcomeBinding.setupLayout() {
        welcomeText.text = String.format(
            getString(R.string.title_welcome_format),
            getString(R.string.app_name)
        )

        nextBtn.setOnClickListener {
            WelcomeFragmentDirections.actionToFirstTodoListFragment().let {
                findNavController().navigate(it)
            }
        }
    }
}