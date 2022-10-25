package com.example.todoappcleanarchwithkoin.ui.todo.addgroup

import com.example.core.data.source.local.model.group.GroupTodoEntity

data class AddGroupState(
    val groupTodoEntity: GroupTodoEntity = GroupTodoEntity()
)