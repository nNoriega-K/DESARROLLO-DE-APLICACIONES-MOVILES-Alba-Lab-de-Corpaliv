package com.example.miapp

import com.example.miapp.data.model.DonacionForm
import com.example.miapp.viewmodel.DonacionValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DonacionValidatorTest {

    @Test
    fun formularioVacio_tieneErroresEnLosCamposObligatorios() {
        val errores = DonacionValidator.validar(DonacionForm())
        assertTrue(errores.hayErrores)
        assertNotNull(errores.nombre)
        assertNotNull(errores.ubicacion)
        assertNotNull(errores.tarjeta)
        assertNotNull(errores.cvv)
    }

    @Test
    fun ubicacionVacia_pideIndicarDesdeDondeDona() {
        val errores = DonacionValidator.validar(DonacionForm(ubicacion = ""))
        assertEquals("Indica desde qué parte de Chile donas", errores.ubicacion)
    }

    @Test
    fun ubicacionDeLaLista_esValidaAunqueSeEscribaSinTildes() {
        assertNull(DonacionValidator.validar(DonacionForm(ubicacion = "Puerto Montt")).ubicacion)
        assertNull(DonacionValidator.validar(DonacionForm(ubicacion = "valparaiso")).ubicacion)
    }

    @Test
    fun ubicacionInventada_pideElegirDeLaLista() {
        val errores = DonacionValidator.validar(DonacionForm(ubicacion = "Narnia"))
        assertEquals("Elige una ciudad o comuna de la lista", errores.ubicacion)
    }

    @Test
    fun luhn_detectaNumerosDeTarjetaMalEscritos() {
        assertTrue(DonacionValidator.cumpleLuhn("4111111111111111"))
        assertFalse(DonacionValidator.cumpleLuhn("4111111111111112"))
    }

    @Test
    fun amex_empiezaCon34o37() {
        assertTrue(DonacionValidator.esAmex("371449635398431"))
        assertFalse(DonacionValidator.esAmex("4111111111111111"))
    }
}
