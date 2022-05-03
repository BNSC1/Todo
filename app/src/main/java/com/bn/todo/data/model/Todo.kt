package com.bn.todo.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.bn.todo.arch.recyclerview.Clickable

@Entity(
    foreignKeys = [ForeignKey(
        entity = TodoList::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("listId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Todo(
    @ColumnInfo val title: String,
    @ColumnInfo val body: String?,
    @ColumnInfo val listId: Int,
    @ColumnInfo val isCompleted: Boolean = false,
//    val locationName: String?,
//    val imageUri: Uri?,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Clickable