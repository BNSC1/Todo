package com.bn.todo.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoList(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo val name: String
)