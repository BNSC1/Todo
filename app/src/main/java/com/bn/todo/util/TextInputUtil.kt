package com.bn.todo.util

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager


object TextInputUtil {
    inline fun setOnFocusChangeListener(
        crossinline focusedAction: () -> Unit = {},
        crossinline unfocusedAction: () -> Unit = {}
    ) = View.OnFocusChangeListener { _, focused ->
        if (focused) {
            focusedAction()
        } else {
            unfocusedAction()
        }

    }

    abstract class TextChangedListener<T>(private val target: T) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            this.onTextChanged(target, s)
        }

        abstract fun onTextChanged(target: T, s: Editable?)
    }

    fun hideKeyboard(context: Context) {
        try {
            (context as Activity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            if (context.currentFocus != null && context.currentFocus!!.windowToken != null) {
                (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    context.currentFocus!!.windowToken, 0
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showKeyboard(context: Context, targetView: View) {
        targetView.requestFocus()
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
            targetView,
            InputMethodManager.SHOW_IMPLICIT
        )
    }
}