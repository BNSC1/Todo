package com.bn.todo.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bn.todo.data.dao.TodoDao
import com.bn.todo.data.dao.TodoListDao
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoList
import com.bn.todo.di.ApplicationModule.context

@Database(entities = [Todo::class, TodoList::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun todoListDao(): TodoListDao

    companion object {
        @Volatile
        private var INSTANCE: TodoDatabase? = null

        fun getInstance(): TodoDatabase =
            INSTANCE?.run { return this } ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    TodoDatabase::class.java,
                    "todoDatabase"
                ).build().also { INSTANCE = it }
            }

    }
}