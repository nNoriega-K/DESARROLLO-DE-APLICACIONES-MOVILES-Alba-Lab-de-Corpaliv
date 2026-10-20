package com.example.miapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donaciones")
data class Donacion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val correo: String,
    val monto: Int,
    val ubicacion: String,                                                 
    val fechaCargo: Long,                      
    val creadaEn: Long                                       
)
