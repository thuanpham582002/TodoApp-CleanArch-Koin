package com.example.todoappcleanarchwithkoin.ui.todo.home

import com.example.core.data.source.local.model.todo.TodoEntity
import com.example.core.domain.model.todo.Todo
import com.example.core.domain.util.TodoOrder

sealed interface HomeEvent {
    data class DeleteToDo(val todoEntity: TodoEntity) : HomeEvent
    data class UpdateToDo(val todoEntity: TodoEntity) : HomeEvent
    data class Order(val todoOrder: TodoOrder) : HomeEvent
    object ToggleOrderSection : HomeEvent
}