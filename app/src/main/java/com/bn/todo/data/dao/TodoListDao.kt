package com.bn.todo.data.dao

import androidx.room.*
import com.bn.todo.data.model.TodoList

@Dao
interface TodoListDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(list: TodoList)

    @Query("Select * from `TodoList`")
    fun query(): List<TodoList>

    @Query("Select * from `TodoList` where `name` = :queryString")
    fun query(queryString: String): List<TodoList>

    @Update
    suspend fun update(list: TodoList)

    @Delete
    suspend fun delete(list: TodoList)
}