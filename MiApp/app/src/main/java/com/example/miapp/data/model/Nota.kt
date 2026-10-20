package com.example.miapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notas")
data class Nota(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val contenido: String,
    val creadaEn: Long                                             
)
