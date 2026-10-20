package com.example.miapp.data.repository

import com.example.miapp.data.local.ComunasChile
import com.example.miapp.data.model.Ubicacion
import java.text.Normalizer

object UbicacionRepository {

    const val LIMITE_SUGERENCIAS = 6

    private val indice: List<Pair<Ubicacion, String>> =
        ComunasChile.todas.map { it to normalizar(it.comuna) }

    fun buscar(texto: String, limite: Int = LIMITE_SUGERENCIAS): List<Ubicacion> {
        val consulta = normalizar(texto)
        if (consulta.isEmpty()) return emptyList()

        val empiezan = indice.filter { it.second.startsWith(consulta) }
        val contienen = indice.filter {
            !it.second.startsWith(consulta) && it.second.contains(consulta)
        }
        return (empiezan + contienen).map { it.first }.take(limite)
    }

    fun encontrarExacta(texto: String): Ubicacion? {
        val consulta = normalizar(texto)
        if (consulta.isEmpty()) return null
        return indice.firstOrNull { it.second == consulta }?.first
    }

    private fun normalizar(texto: String): String =
        Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
            .lowercase()
            .trim()
            .replace(Regex("\\s+"), " ")
}
