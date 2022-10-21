package com.example.todoappcleanarchwithkoin.ui.todo.home

import com.example.core.data.source.local.model.todo.TodoEntity
import com.example.core.domain.util.TodoOrder

sealed interface HomeEvent {
    data class DeleteToDo(val todoEntity: TodoEntity) : HomeEvent
    data class UpdateToDo(val todoEntity: TodoEntity) : HomeEvent
    data class SaveGroup(val groupName: String) : HomeEvent
    data class Order(val todoOrder: TodoOrder) : HomeEvent
    data class CurrentGroupIndex(val groupIndex: Int) : HomeEvent
    data class SearchQueryChange(val newText: String) : HomeEvent
    object ToggleOrderSection : HomeEvent
}