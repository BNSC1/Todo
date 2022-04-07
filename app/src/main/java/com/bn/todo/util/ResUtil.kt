package com.bn.todo.util

import android.content.Context
import android.util.TypedValue

object ResUtil {
    fun getAttribute(context: Context, attrId: Int) = TypedValue().apply {
        context.theme.resolveAttribute(
            attrId,
            this,
            true
        )
    }.data
}