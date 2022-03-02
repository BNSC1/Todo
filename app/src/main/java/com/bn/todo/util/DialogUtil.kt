package com.bn.todo.util

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.bn.todo.R
import com.bn.todo.databinding.LayoutListNameInputBinding

object DialogUtil {
    fun showDialog(context: Context, title: String?, msg: String): AlertDialog =
        AlertDialog.Builder(context).apply {
            title?.let { setTitle(it) }
            setMessage(msg)
            setPositiveButton(context.getString(R.string.action_ok)) { _, _ -> }
            setCancelable(false)
        }.show()

    fun showInputDialog(
        context: Context,
        title: String? = null,
        hint: String? = null,
        inputReceiver: OnInputReceiver
    ): AlertDialog =
        AlertDialog.Builder(context).apply {
            val inflater = LayoutInflater.from(context)
            val binding = LayoutListNameInputBinding.inflate(inflater)

            title?.let { setTitle(it) }
            setView(binding.root)
            hint?.let { binding.listNameInput.hint = it }
            setPositiveButton(android.R.string.ok) { d, _ ->
                inputReceiver.receiveInput(binding.listNameInput.text.toString())
                d.dismiss()
            }
            setNegativeButton(android.R.string.cancel) { d, _ -> d.dismiss() }

            create()
        }.show()

    interface OnInputReceiver {
        fun receiveInput(input: String?)
    }
}