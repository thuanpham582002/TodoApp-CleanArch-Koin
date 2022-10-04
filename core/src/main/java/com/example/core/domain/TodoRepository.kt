package com.example.core.domain

import androidx.lifecycle.LiveData
import com.example.core.data.source.local.model.group.GroupTodoEntity
import com.example.core.data.source.local.model.relation.GroupWithTodos
import com.example.core.data.source.local.model.todo.TodoEntity
import com.example.core.domain.model.group.GroupTodo
import com.example.core.domain.model.todo.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getAllTodo(): Flow<List<TodoEntity>>
    fun getTodoById(id: Long): TodoEntity
    fun getAllTodoWithGroup(groupName: String): Flow<GroupWithTodos>
    fun getAllGroup(): Flow<List<GroupTodoEntity>>
    suspend fun deleteTodo(toDo: TodoEntity)
    suspend fun insertTodo(toDo: TodoEntity)
    suspend fun insertGroup(group: GroupTodoEntity)
    suspend fun updateTodo(toDo: TodoEntity)
    suspend fun deleteAllTodo()
    suspend fun deleteAllCompletedTodo()
    suspend fun deleteGroupTodo(groupTodoEntity: GroupTodoEntity)
}