package com.example.core.data.source.local.model.group

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "group_todo_table")
data class GroupTodoEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "group_name")
    val name: String
)

