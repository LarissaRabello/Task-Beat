package com.devspace.taskbeats

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity // Anotação
data class CategoryEntity(
    @PrimaryKey                   // Incremental, 0, 1, 2, 3 - id é a identificação da categoria
    @ColumnInfo("key", )
    val name: String,                   // a chave da categoria será o nome dela
    @ColumnInfo("is_selected")
    val isSelected: Boolean             // se a coluna está selecionada, mudará o background
)