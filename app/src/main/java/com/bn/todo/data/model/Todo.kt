package com.bn.todo.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(
    foreignKeys = [ForeignKey(
        entity = TodoList::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("listId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Todo(
    val title: String,
    val body: String?,
    @ColumnInfo(index = true) val listId: Long,
    val isCompleted: Boolean = false,
    val createdTime: OffsetDateTime? = null,
//    val locationName: String?,
//    val imageUri: Uri?,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)