package com.bn.todo.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bn.todo.data.model.Todo

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(todo: Todo)

    @Query("Select * from `Todo`")
    fun query(): LiveData<List<Todo>>

    @Query("Select * from `Todo` where `title` = :queryString")
    fun query(queryString: String): LiveData<List<Todo>>

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)
}