package com.example.miapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.miapp.data.model.Nota
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {

    @Query("SELECT * FROM notas ORDER BY creadaEn DESC, id DESC")
    fun observarTodas(): Flow<List<Nota>>

    @Insert
    suspend fun insertar(nota: Nota): Long

    @Delete
    suspend fun eliminar(nota: Nota)
}
