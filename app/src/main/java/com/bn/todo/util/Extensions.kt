package com.bn.todo.util

fun CharSequence?.getTextOrDefault(default: String) = if (this.isNullOrBlank()) default else this