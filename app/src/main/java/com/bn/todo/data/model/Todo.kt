package com.bn.todo.data.model

import android.net.Uri

data class Todo(
    val id: Long,
    val title: String,
    val body: String?,
    val locationName: String?,
    val imageUri: Uri?
)