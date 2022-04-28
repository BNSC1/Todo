package com.bn.todo.util

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.bn.todo.R
import com.bn.todo.databinding.LayoutTextInputBinding


object DialogUtil {
    fun showDialog(context: Context, title: String? = null, msg: String): AlertDialog =
        AlertDialog.Builder(context).apply {
            title?.let { setTitle(it) }
            setMessage(msg)
            setPositiveButton(android.R.string.ok) { _, _ -> }
        }.show()

    fun showConfirmDialog(
        context: Context,
        title: String? = null,
        msg: String,
        okAction: () -> Unit,
        cancelAction: () -> Unit = {}
    ): AlertDialog =
        AlertDialog.Builder(context).apply {
            title?.let { setTitle(it) }
            setMessage(msg)
            setPositiveButton(context.getString(android.R.string.ok)) { _, _ -> okAction() }
            setNegativeButton(context.getString(android.R.string.cancel)) { _, _ -> cancelAction() }
        }.show()

    fun showInputDialog(
        context: Context,
        title: String? = null,
        hint: String? = null,
        defaultValue: String? = null,
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
                defaultValue?.let { input.setText(it) }
                input.requestFocus()
            }

            create()
        }.show()

    inline fun showRadioDialog(
        context: Context,
        items: Array<String>,
        crossinline okAction: (Int) -> Unit,
        title: String? = null,
        defaultIndex: Int = 0

    ): AlertDialog = AlertDialog.Builder(context).apply {
        title?.let { setTitle(it) }
        var clickedIndex = defaultIndex
        setSingleChoiceItems(items, defaultIndex) { _, index ->
            clickedIndex = index
        }
        create().apply {
            setNegativeButton(android.R.string.cancel) { _, _ ->
                dismiss()
            }
            setPositiveButton(android.R.string.ok) { _, _ ->
                okAction(clickedIndex)
            }
        }
    }.show()

    interface OnInputReceiver {
        fun receiveInput(input: String?)
    }
}