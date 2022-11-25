package com.bn.todo.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bn.todo.data.dao.TodoDao
import com.bn.todo.data.dao.TodoListDao
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoList
import com.bn.todo.util.OffsetDateTimeConverters

@Database(
    entities = [Todo::class, TodoList::class], version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)]
)
@TypeConverters(OffsetDateTimeConverters::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun todoListDao(): TodoListDao
}