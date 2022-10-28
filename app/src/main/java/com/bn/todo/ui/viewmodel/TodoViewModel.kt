package com.bn.todo.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.bn.todo.arch.BaseViewModel
import com.bn.todo.arch.ViewModelMessage
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoFilter
import com.bn.todo.data.model.TodoList
import com.bn.todo.data.repository.TodoRepository
import com.bn.todo.data.repository.UserPrefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val userPrefRepository: UserPrefRepository
) : BaseViewModel() {

    private val _todoLists: StateFlow<List<TodoList>>
    val todoLists get() = _todoLists
    private val _currentList: StateFlow<TodoList?>
    val currentList get() = _currentList

    private val _clickedTodo = MutableStateFlow<Todo?>(null)
    val clickedTodo get() = _clickedTodo
    private val _listCount = MutableStateFlow(-1)
    val listCount get() = _listCount

    init {
        _todoLists = getTodoListFlow()
        _currentList = getCurrentListFlow()
    }

    fun insertTodoList(name: String) = tryRun {
        setCurrentList(id = todoRepository.insertTodoList(name))
        if (userPrefRepository.getIsFirstTimeLaunch().first()) {
            userPrefRepository.setIsFirstTimeLaunch(false)
        }
    }

    private fun getTodoListFlow(name: String? = null) =
        todoRepository.queryTodoList(name).onEach { _listCount.value = it.size }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun updateTodoList(list: TodoList, name: String) = tryRun {
        todoRepository.updateTodoList(list, name)
    }

    fun deleteTodoList(list: TodoList) = tryRun {
        setCurrentList()
        todoRepository.deleteTodoList(list)
    }

    fun insertTodo(title: String, body: String?) = tryRun {
        currentList.value?.id?.let { todoRepository.insertTodo(title, body, it) }
    }

    @OptIn(FlowPreview::class)
    fun queryTodo(name: String? = null) =
        getFilterFlow().combine(getSortPrefFlow()) { filter, sortPref ->
            todoRepository.queryTodo(filter, sortPref)
        }.catch {
            _message.emit(ViewModelMessage.Error(it.message.toString()))
        }.flattenMerge(Int.MAX_VALUE)

    private fun getFilterFlow() =
        getCurrentListIdFlow().combine(getShowCompleted()) { id, showCompleted ->
            TodoFilter(id, showCompleted)
        }

    fun updateTodo(todo: Todo, name: String, body: String) = tryRun {
        todoRepository.updateTodo(todo, name, body)
    }

    fun updateTodo(todo: Todo, isCompleted: Boolean) = tryRun {
        todoRepository.updateTodo(todo, isCompleted)
    }

    fun deleteTodo(todo: Todo) = tryRun {
        todoRepository.deleteTodo(todo)
    }

    fun deleteCompletedTodos() = tryRun {
        val deletedTodoCount = todoRepository.deleteCompletedTodo(getCurrentListIdFlow().first())
        _message.emit(ViewModelMessage.Info.CompletedTodoDeletion(deletedTodoCount))
    }

    fun setClickedTodo(todo: Todo) {
        _clickedTodo.value = todo
    }

    private fun getCurrentListIdFlow() = userPrefRepository.getCurrentListId(0).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = 0
    )

    fun setCurrentList(list: TodoList? = null, id: Long? = null) = tryRun {
        (list?.id ?: id ?: todoLists.value.firstOrNull()?.id)?.let {
            userPrefRepository.setCurrentListId(it)
        }
    }

    fun getShowCompleted(default: Boolean = true) =
        userPrefRepository.getShowCompleted(default)

    fun setShowCompleted(showCompleted: Boolean) = tryRun {
        userPrefRepository.setShowCompleted(showCompleted)
    }

    private fun getCurrentListFlow() = todoLists.combine(getCurrentListIdFlow()) { list, id ->
        list.firstOrNull { it.id == id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    fun setSortPref(sortPref: Int) = tryRun {
        userPrefRepository.setSortPref(sortPref)
    }

    fun getSortPrefFlow(default: Int = 0) =
        userPrefRepository.getSortPref(default)
}