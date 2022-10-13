package com.example.todoappcleanarchwithkoin.ui.todo.home

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.source.local.model.todo.TodoEntity
import com.example.core.domain.use_case.TodoUseCase
import com.example.core.domain.util.TodoOrder
import com.example.todoappcleanarchwithkoin.ui.notification.TodoScheduler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get

class HomeViewModel(private val todoUseCase: TodoUseCase) : ViewModel() {
    val listGroup = todoUseCase.getAllGroupTodoEntity()
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state
    val todoScheduler: TodoScheduler = get(TodoScheduler::class.java)

    private var getTodosJob: Job? = null

    init {
        getTodos(TodoOrder.Order())
    }

    private fun getTodos(order: TodoOrder.Order) {
        getTodosJob?.cancel()
        getTodosJob = todoUseCase.getAllTodoEntity(order).onEach { todos ->
            Log.i("HomeViewModel", "getTodos:")
            _state.value = _state.value.copy(
                listTodo = todos,
                todoOrder = order
            )
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.DeleteToDo -> {
                viewModelScope.launch {
                    todoUseCase.deleteTodoEntity(event.todoEntity)
                    todoScheduler.todoCancelAlarmManager(event.todoEntity.id)
                }
            }
            is HomeEvent.Order -> {
                when (event.todoOrder) {
                    is TodoOrder.GroupType -> {
                        getTodos(_state.value.todoOrder.copy(groupType = event.todoOrder))
                    }

                    is TodoOrder.Order -> TODO()
                    is TodoOrder.OrderBy -> {
                        getTodos(_state.value.todoOrder.copy(orderBy = event.todoOrder))
                    }
                    is TodoOrder.OrderType -> {
                        getTodos(_state.value.todoOrder.copy(orderType = event.todoOrder))

                    }
                    is TodoOrder.TodoType -> {
                        getTodos(_state.value.todoOrder.copy(todoType = event.todoOrder))
                    }
                }
            }
            is HomeEvent.ToggleOrderSection -> {
                _state.value =
                    _state.value.copy(isOrderSectionVisible = !state.value.isOrderSectionVisible)
            }
            is HomeEvent.UpdateToDo -> {
                viewModelScope.launch {
                    todoUseCase.updateTodoEntity(event.todoEntity)

                }
            }
            is HomeEvent.CurrentGroupName -> {
                _state.value = _state.value.copy(currentGroupName = event.groupName)
            }
        }

    }

    fun saveStateTodo(todoEntity: TodoEntity) {
        val savedStateHandle: SavedStateHandle = get(SavedStateHandle::class.java)
        savedStateHandle["todoEntity"] = todoEntity
    }
}