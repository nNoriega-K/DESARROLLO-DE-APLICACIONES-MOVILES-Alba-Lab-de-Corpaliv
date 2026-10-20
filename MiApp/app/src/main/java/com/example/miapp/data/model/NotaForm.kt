package com.example.miapp.data.model

data class NotaForm(
    val titulo: String = "",
    val contenido: String = ""
)

enum class CampoNota { Titulo, Contenido }

data class NotaErrores(
    val titulo: String? = null,
    val contenido: String? = null
) {
    val hayErrores: Boolean get() = titulo != null || contenido != null

    fun soloDe(campos: Set<CampoNota>) = NotaErrores(
        titulo = titulo.takeIf { CampoNota.Titulo in campos },
        contenido = contenido.takeIf { CampoNota.Contenido in campos }
    )
}
