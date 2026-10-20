package com.example.miapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.miapp.data.model.Donacion
import kotlinx.coroutines.flow.Flow

@Dao
interface DonacionDao {

    @Insert
    suspend fun insertar(donacion: Donacion): Long

    @Delete
    suspend fun eliminar(donacion: Donacion)

    @Query("SELECT * FROM donaciones ORDER BY id DESC")
    fun observarTodas(): Flow<List<Donacion>>
}
