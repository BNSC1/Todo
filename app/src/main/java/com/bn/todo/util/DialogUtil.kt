package com.bn.todo.util

import android.app.AlertDialog
import android.content.Context
import com.bn.todo.R

object DialogUtil {
    fun showDialog(context: Context, title: String?, msg: String): AlertDialog? {
        val builder = AlertDialog.Builder(context)
        title?.let { builder.setTitle(it) }
        builder.setMessage(msg)
        builder.setPositiveButton(context.getString(R.string.action_ok)) { _, _ -> }
        builder.setCancelable(false)
        return builder.show()
    }
}