package com.example.miapp.data.repository

import com.example.miapp.data.local.DonacionDao
import com.example.miapp.data.model.Donacion
import com.example.miapp.data.model.DonacionForm
import kotlinx.coroutines.flow.Flow

class DonacionRepository(private val dao: DonacionDao) {

    val donaciones: Flow<List<Donacion>> = dao.observarTodas()

    suspend fun guardar(form: DonacionForm) {
        dao.insertar(
            Donacion(
                nombre = form.nombre.trim(),
                correo = form.correo.trim(),
                monto = form.monto.toInt(),
                destino = form.destino,
                mensual = form.mensual,
                fechaCargo = requireNotNull(form.fechaCargo),
                creadaEn = System.currentTimeMillis()
            )
        )
    }

    suspend fun eliminar(donacion: Donacion) = dao.eliminar(donacion)
}
