package com.bn.todo.util

import androidx.fragment.app.Fragment

fun CharSequence?.getTextOrDefault(default: String) = if (this.isNullOrBlank()) default else this

fun Fragment.showDialog(titleStringId: Int? = null, messageStringId: Int) =
    DialogUtil.showDialog(
        requireActivity(),
        titleStringId?.let { getString(it) },
        getString(messageStringId)
    )

fun Fragment.showDialog(title: String? = null, message: String) =
    DialogUtil.showDialog(requireActivity(), title, message)