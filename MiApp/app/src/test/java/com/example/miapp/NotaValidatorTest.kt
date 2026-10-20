package com.example.miapp

import com.example.miapp.data.model.CampoNota
import com.example.miapp.data.model.NotaForm
import com.example.miapp.viewmodel.NotaValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NotaValidatorTest {

    @Test
    fun formularioVacio_tieneErroresEnAmbosCampos() {
        val errores = NotaValidator.validar(NotaForm())
        assertNotNull(errores.titulo)
        assertNotNull(errores.contenido)
        assertTrue(errores.hayErrores)
    }

    @Test
    fun soloEspacios_seConsideraVacio() {
        val errores = NotaValidator.validar(NotaForm(titulo = "     ", contenido = "     "))
        assertEquals("El título es obligatorio", errores.titulo)
        assertEquals("El contenido es obligatorio", errores.contenido)
    }

    @Test
    fun tituloMuyCorto_daError() {
        val errores = NotaValidator.validar(NotaForm(titulo = "ab", contenido = "contenido valido"))
        assertNotNull(errores.titulo)
        assertNull(errores.contenido)
    }

    @Test
    fun contenidoMuyCorto_daError() {
        val errores = NotaValidator.validar(NotaForm(titulo = "Titulo", contenido = "abc"))
        assertNull(errores.titulo)
        assertNotNull(errores.contenido)
    }

    @Test
    fun formularioCorrecto_noTieneErrores() {
        val errores = NotaValidator.validar(NotaForm(titulo = "Compras", contenido = "Pan y leche"))
        assertFalse(errores.hayErrores)
    }

    @Test
    fun soloDe_ocultaLosErroresDeCamposNoTocados() {
        val errores = NotaValidator.validar(NotaForm()).soloDe(setOf(CampoNota.Titulo))
        assertNotNull(errores.titulo)
        assertNull(errores.contenido)
    }
}
