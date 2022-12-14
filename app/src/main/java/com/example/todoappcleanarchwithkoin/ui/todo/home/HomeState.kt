package com.example.todoappcleanarchwithkoin.ui.todo.home

import com.example.core.data.source.local.model.group.GroupTodoEntity
import com.example.core.data.source.local.model.todo.TodoEntity
import com.example.core.domain.util.TodoOrder

data class HomeState(
    val listTodo: List<TodoEntity> = emptyList(),
    val searchQuery: String = "",
    val todoOrder: TodoOrder.Order = TodoOrder.Order(),
    val isOrderSectionVisible: Boolean = false,
)
