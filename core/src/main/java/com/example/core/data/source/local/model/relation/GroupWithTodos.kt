package com.example.core.data.source.local.model.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.core.data.source.local.model.group.GroupTodoEntity
import com.example.core.data.source.local.model.todo.TodoEntity


data class GroupWithTodos(
    @Embedded val group: GroupTodoEntity,  // Embedded annotation is used to tell Room that this is a nested object
    @Relation(                      // Relation annotation is used to tell Room that this is a relation
        parentColumn = "group_name",
        entityColumn = "group_name"
    )
    val todos: List<TodoEntity>
)
