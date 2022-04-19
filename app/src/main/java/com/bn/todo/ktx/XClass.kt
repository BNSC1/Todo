package com.bn.todo.ktx

import android.content.res.Resources

fun CharSequence?.getTextOrDefault(default: String) = if (this.isNullOrBlank()) default else this

fun Resources.getNavigationSize(): Int {
    var navigationBarHeight = 0
    val resourceId: Int = getIdentifier("navigation_bar_height", "dimen", "android")
    if (resourceId > 0) {
        navigationBarHeight = getDimensionPixelSize(resourceId)
    }
    return navigationBarHeight
}