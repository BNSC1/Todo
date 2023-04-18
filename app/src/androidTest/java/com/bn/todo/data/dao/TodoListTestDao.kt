package com.bn.todo.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.bn.todo.testutil.InstantTaskExecutorExtension
import com.bn.todo.data.db.TodoDatabase
import com.bn.todo.data.model.TodoList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldNotContain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class)
internal class TodoListTestDao {
    private lateinit var todoListDao: TodoListDao
    private lateinit var todoDatabase: TodoDatabase
    private val listName = "listName"

    @BeforeEach
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        todoDatabase = Room.inMemoryDatabaseBuilder(context, TodoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        todoListDao = todoDatabase.todoListDao()
    }

    @AfterEach
    fun closeDB() {
        todoDatabase.close()
    }

    @Test
    fun insertList() = runTest {
        val list = TodoList(listName, 1)

        todoListDao.insert(list)

        todoListDao.query().first() shouldContain list
    }

    @Test
    fun deleteList() = runTest {
        val list = TodoList(listName, 1)
        todoListDao.insert(list)
        todoListDao.query().first() shouldContain list

        todoListDao.delete(list)

        todoListDao.query().first() shouldNotContain list
    }

    @Test
    fun updateList() = runTest {
        val list = TodoList(listName, 1)
        todoListDao.insert(list)
        todoListDao.query().first() shouldContain list
        val newName = "newName"
        val newList = list.copy(newName)

        todoListDao.update(newList)

        todoListDao.query().first().let {
            it shouldHaveSize 1
            it shouldContain newList
        }
    }

    @Test
    fun queryLists() = runTest {
        val lists = listOf(TodoList(listName, 1), TodoList(listName, 2), TodoList(listName, 3))
        lists.forEach {
            todoListDao.insert(it)
        }

        val res = todoListDao.query().first()

        res shouldContainAll lists
    }

    @Test
    fun queryList() = runTest {
        val l1 = TodoList(listName, 1)
        val l2 = TodoList("l2", 2)
        val l3 = TodoList("l3", 3)
        val lists = listOf(l1, l2, l3)
        lists.forEach {
            todoListDao.insert(it)
        }

        val res = todoListDao.query(listName).first()

        res shouldHaveSize 1
        res shouldContain l1
    }
}