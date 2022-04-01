package com.bn.todo.util

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.bn.todo.R
import com.bn.todo.databinding.LayoutTextInputBinding


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
            LayoutTextInputBinding.inflate(inflater).apply {
                root.hint = context.getString(R.string.list_name)

                title?.let { setTitle(it) }
                setView(root)
                hint?.let { input.hint = it }
                setPositiveButton(android.R.string.ok) { d, _ ->
                    inputReceiver.receiveInput(input.text.toString())
                    d.dismiss()
                }
                setNegativeButton(android.R.string.cancel) { d, _ -> d.dismiss() }
                input.requestFocus()
            }

            create()
        }.show()

    interface OnInputReceiver {
        fun receiveInput(input: String?)
    }
}