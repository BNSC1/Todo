package com.bn.todo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoList(
    val name: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)