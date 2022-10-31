package com.bn.todo.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.bn.todo.R
import com.bn.todo.arch.BaseViewModel
import com.bn.todo.arch.ViewModelMessage
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoFilter
import com.bn.todo.data.model.TodoList
import com.bn.todo.data.model.TodoSort
import com.bn.todo.data.repository.TodoRepository
import com.bn.todo.data.repository.UserPrefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private val _sortPref: StateFlow<TodoSort>
    val sortPref get() = _sortPref
    private val _currentTodos: StateFlow<List<Todo>?>
    val currentTodos get() = _currentTodos
    private val _showCompleted: StateFlow<Boolean>
    val showCompleted get() = _showCompleted

    private val _clickedTodo = MutableStateFlow<Todo?>(null)
    val clickedTodo get() = _clickedTodo
    private val _listCount = MutableStateFlow(-1)
    val listCount get() = _listCount

    init {
        _todoLists = getTodoListFlow()
        _currentList = getCurrentListFlow()
        _sortPref = getSortPrefFlow()
        _currentTodos = queryTodo()
        _showCompleted = getShowCompletedFlow()
    }

    fun insertTodoList(name: String) = tryRun {
        setCurrentList(id = todoRepository.insertTodoList(name))
        if (userPrefRepository.getIsFirstTimeLaunch().first()) {
            userPrefRepository.setIsFirstTimeLaunch(false)
        }
    }

    private fun getTodoListFlow(name: String? = null) =
        todoRepository.queryTodoList(name).onEach { _listCount.value = it.size }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )

    fun updateTodoList(list: TodoList, name: String) = tryRun {
        todoRepository.updateTodoList(list, name)
    }

    fun deleteTodoList(list: TodoList) = tryRun {
        todoRepository.deleteTodoList(list)
    }

    fun insertTodo(title: String, body: String?) = tryRun {
        currentList.value?.id?.let { todoRepository.insertTodo(title, body, it) }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun queryTodo(name: String? = null) =
        getFilterFlow().flatMapLatest { filter ->
            todoRepository.queryTodoFlow(filter)
        }
            .distinctUntilChanged()
            .catch {
                _message.emit(ViewModelMessage.Error(it.message.toString()))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )

    private fun getFilterFlow() =
        getCurrentListIdFlow().combine(getShowCompletedFlow()) { id, showCompleted ->
            TodoFilter(id, showCompleted)
        }.distinctUntilChanged()


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
        val deletedTodoCount = currentList.value?.id?.let {
            todoRepository.deleteCompletedTodo(it)
        }
        _message.emit(deletedTodoCount?.let {
            ViewModelMessage.Info.CompletedTodoDeletion(it)
        } ?: ViewModelMessage.Error(msgStringId = R.string.error_missing_current_list)
        )
    }

    fun setClickedTodo(todo: Todo) {
        _clickedTodo.value = todo
    }

    private fun getCurrentListIdFlow() = userPrefRepository.getCurrentListId(0)
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0
        )

    fun setCurrentList(list: TodoList? = null, id: Long? = null) = tryRun {
        (list?.id ?: id)?.let {
            userPrefRepository.setCurrentListId(it)
        }
    }

    private fun getShowCompletedFlow(default: Boolean = true) =
        userPrefRepository.getShowCompleted(default)
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = false
            )

    fun setShowCompleted(showCompleted: Boolean) = tryRun {
        userPrefRepository.setShowCompleted(showCompleted)
    }

    private fun getCurrentListFlow() = todoLists.combine(getCurrentListIdFlow()) { list, id ->
        list.firstOrNull { it.id == id } ?: todoLists.value.firstOrNull()
    }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    fun setSortPref(sortPref: Int) = tryRun {
        userPrefRepository.setSortPref(sortPref)
    }

    private fun getSortPrefFlow(default: Int = 0) =
        userPrefRepository.getSortPref(default).map { pref ->
            TodoSort.values().first { it.ordinal == pref }
        }.distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = TodoSort.values()[0]
            )
}