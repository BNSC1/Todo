package com.bn.todo.ktx

fun CharSequence?.getTextOrDefault(default: String) = if (this.isNullOrBlank()) default else this
