package com.bn.todo.ui.welcome.view

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import com.bn.todo.R
import com.bn.todo.arch.BaseFragment
import com.bn.todo.arch.HidesActionBar
import com.bn.todo.databinding.FragmentWelcomeBinding
import com.bn.todo.ui.theme.PaddingLargeButton
import com.bn.todo.ui.theme.PaddingTitleLayout
import com.bn.todo.ui.theme.TodoTheme
import com.bn.todo.ui.theme.WelcomeCoverRatio

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
        composeView.setContent {
            TodoTheme(dynamicColor = false) {
                Column {
                    WelcomeBox()
                    Spacer(Modifier.weight(1f))
                    TextButton(modifier = Modifier
                        .align(End)
                        .padding(PaddingLargeButton),
                        onClick = { goToFirstTodoList() }) {
                        Text(style = MaterialTheme.typography.titleLarge,text = stringResource(id = R.string.action_next).uppercase())
                    }
                }
            }
        }
    }

    private fun goToFirstTodoList() {
        WelcomeFragmentDirections.actionToFirstTodoListFragment().let {
            findNavController().navigate(it)
        }
    }

    @Composable
    private fun WelcomeBox() {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .fillMaxWidth()
                .height((screenHeight * WelcomeCoverRatio).dp),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.weight(1f))
            Text(
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                text = stringResource(
                    R.string.title_welcome_format, stringResource(R.string.app_name)
                )
            )
            Spacer(Modifier.weight(1f))
            Text(
                modifier = Modifier.padding(bottom = PaddingTitleLayout),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                text = stringResource(id = R.string.title_welcome_sub))
        }
    }
}