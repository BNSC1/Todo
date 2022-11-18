package com.bn.todo.arch

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.bn.todo.R
import com.bn.todo.ktx.collectLatestLifecycleFlow
import com.bn.todo.util.DialogUtil.showDialog

interface CollectsViewModelMessage {
    val viewModel: BaseViewModel

    fun Fragment.collectMessage() = collectMessage(requireContext(), viewLifecycleOwner)

    fun AppCompatActivity.collectMessage() = collectMessage(this, this)

    private fun collectMessage(context: Context, lifecycleOwner: LifecycleOwner) {
        viewModel.message.collectLatestLifecycleFlow(lifecycleOwner) { msg ->
            when (msg) {
                is ViewModelMessage.Error -> {
                    showDialog(context,
                        msg = msg.msg ?: msg.msgStringId?.let { context.getString(it) }
                        ?: context.getString(R.string.err_unknown)
                    )
                }
                is ViewModelMessage.Info.CompletedTodoDeletion ->
                    Toast.makeText(
                        context,
                        context.getString(R.string.msg_deleted_todos_format).format(msg.count),
                        Toast.LENGTH_LONG
                    ).show()
            }
        }
    }
}