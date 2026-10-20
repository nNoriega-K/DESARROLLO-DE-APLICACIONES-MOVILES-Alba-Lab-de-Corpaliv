package com.example.miapp.data.repository

import com.example.miapp.data.local.NotaDao
import com.example.miapp.data.model.Nota
import com.example.miapp.data.model.NotaForm
import kotlinx.coroutines.flow.Flow

class NotaRepository(private val dao: NotaDao) {

    val notas: Flow<List<Nota>> = dao.observarTodas()

    suspend fun guardar(form: NotaForm) {
        dao.insertar(
            Nota(
                titulo = form.titulo.trim(),
                contenido = form.contenido.trim(),
                creadaEn = System.currentTimeMillis()
            )
        )
    }

    suspend fun eliminar(nota: Nota) = dao.eliminar(nota)
}
