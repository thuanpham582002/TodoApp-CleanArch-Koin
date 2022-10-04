package com.example.core.domain.model.group

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class GroupTodo(
    val name: String,
)

class InvalidGroupException(message: String) : Exception(message)
