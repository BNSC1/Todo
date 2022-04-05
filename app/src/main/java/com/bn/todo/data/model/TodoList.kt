package com.bn.todo.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoList(
    @ColumnInfo val name: String,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)