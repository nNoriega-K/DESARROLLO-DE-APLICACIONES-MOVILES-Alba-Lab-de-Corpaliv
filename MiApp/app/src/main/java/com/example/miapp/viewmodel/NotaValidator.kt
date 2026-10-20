package com.example.miapp.viewmodel

import com.example.miapp.data.model.NotaErrores
import com.example.miapp.data.model.NotaForm

object NotaValidator {

    const val TITULO_MIN = 3
    const val TITULO_MAX = 50
    const val CONTENIDO_MIN = 5
    const val CONTENIDO_MAX = 500

    fun validar(form: NotaForm) = NotaErrores(
        titulo = validarTitulo(form.titulo),
        contenido = validarContenido(form.contenido)
    )

    private fun validarTitulo(texto: String): String? {
        val titulo = texto.trim()
        return when {
            titulo.isEmpty() -> "El título es obligatorio"
            titulo.length < TITULO_MIN -> "El título debe tener al menos $TITULO_MIN caracteres"
            else -> null
        }
    }

    private fun validarContenido(texto: String): String? {
        val contenido = texto.trim()
        return when {
            contenido.isEmpty() -> "El contenido es obligatorio"
            contenido.length < CONTENIDO_MIN -> "El contenido debe tener al menos $CONTENIDO_MIN caracteres"
            else -> null
        }
    }
}
