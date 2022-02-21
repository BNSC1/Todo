package com.bn.todo.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bn.todo.data.model.TodoList

@Dao
interface TodoListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: TodoList)

    @Query("Select * from `TodoList`")
    fun query(): LiveData<List<TodoList>>

    @Query("Select * from `TodoList` where `name` = :queryString")
    fun query(queryString: String): LiveData<List<TodoList>>

    @Update
    fun update(list: TodoList)

    @Delete
    fun delete(list: TodoList)
}