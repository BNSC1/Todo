package com.bn.todo.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bn.todo.data.dao.TodoDao
import com.bn.todo.data.dao.TodoListDao
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoList

@Database(entities = [Todo::class, TodoList::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun todoListDao(): TodoListDao
}