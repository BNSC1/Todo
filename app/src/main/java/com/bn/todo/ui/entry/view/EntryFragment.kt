package com.bn.todo.ui.entry.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bn.todo.arch.CollectsViewModelMessage
import com.bn.todo.arch.HasNavigation
import com.bn.todo.arch.NavigationActivity
import com.bn.todo.ktx.collectLatestLifecycleFlow
import com.bn.todo.ui.entry.viewmodel.EntryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EntryFragment : Fragment(), CollectsViewModelMessage, HasNavigation {
    override val _activity get() = activity as? NavigationActivity
    override val viewModel: EntryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectIsFirstLaunch {
            handleFirstLaunchNavigation(it)
        }
    }

    private fun collectIsFirstLaunch(onCollectedAction: (Boolean) -> Unit) {
        viewModel.isFirstLaunch.collectLatestLifecycleFlow(this) { isFirstLaunch ->
            onCollectedAction(isFirstLaunch)
        }
    }

    private fun handleFirstLaunchNavigation(isFirstLaunch: Boolean) {
        val direction = EntryFragmentDirections.run {
            if (isFirstLaunch) {
                actionToWelcomeFragment()
            } else {
                actionToListFragment()
            }
        }
        direction.navigate()
    }

}