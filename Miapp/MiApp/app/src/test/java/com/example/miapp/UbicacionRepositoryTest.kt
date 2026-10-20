package com.example.miapp

import com.example.miapp.data.local.ComunasChile
import com.example.miapp.data.repository.UbicacionRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UbicacionRepositoryTest {

    @Test
    fun hayLas346ComunasDeChile_sinRepetir() {
        assertEquals(346, ComunasChile.todas.size)
        assertEquals(346, ComunasChile.todas.map { it.comuna }.toSet().size)
    }

    @Test
    fun buscar_primeroLasQueEmpiezanConElTexto() {
        val resultado = UbicacionRepository.buscar("puerto m")
        assertEquals("Puerto Montt", resultado.first().comuna)
    }

    @Test
    fun buscar_ignoraTildesYMayusculas() {
        val comunas = UbicacionRepository.buscar("NUNOA").map { it.comuna }
        assertTrue("Ñuñoa" in comunas)
    }

    @Test
    fun buscar_muestraTodasLasQueEmpiezanIgual() {
        val comunas = UbicacionRepository.buscar("san pedro").map { it.comuna }
        assertTrue("San Pedro" in comunas)
        assertTrue("San Pedro de Atacama" in comunas)
        assertTrue("San Pedro de la Paz" in comunas)
    }

    @Test
    fun buscar_respetaElLimiteYElTextoVacio() {
        assertEquals(3, UbicacionRepository.buscar("san", limite = 3).size)
        assertTrue(UbicacionRepository.buscar("").isEmpty())
        assertTrue(UbicacionRepository.buscar("   ").isEmpty())
    }

    @Test
    fun encontrarExacta_aceptaVariacionesDeEscritura() {
        assertEquals("Santiago", UbicacionRepository.encontrarExacta("  SANTIAGO ")?.comuna)
        assertEquals("Valparaíso", UbicacionRepository.encontrarExacta("valparaiso")?.comuna)
        assertNotNull(UbicacionRepository.encontrarExacta("Puerto Montt"))
    }

    @Test
    fun encontrarExacta_rechazaLoQueNoExiste() {
        assertNull(UbicacionRepository.encontrarExacta("Narnia"))
        assertNull(UbicacionRepository.encontrarExacta("puerto"))
        assertNull(UbicacionRepository.encontrarExacta(""))
    }
}
