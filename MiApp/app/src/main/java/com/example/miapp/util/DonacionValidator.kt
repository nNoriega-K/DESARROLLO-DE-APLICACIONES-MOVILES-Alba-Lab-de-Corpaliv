package com.example.miapp.util

import com.example.miapp.data.model.DonacionErrores
import com.example.miapp.data.model.DonacionForm
import java.util.Calendar

object DonacionValidator {

    const val MONTO_MINIMO = 1_000
    const val MONTO_MAXIMO = 1_000_000

    private val REGEX_NOMBRE = Regex("^[\\p{L}][\\p{L} .'-]*$")
    private val REGEX_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validar(
        f: DonacionForm,
        hoyUtc: Long = hoyUtcMillis(),
        ahora: Calendar = Calendar.getInstance()
    ) = DonacionErrores(
        nombre = validarNombre(f.nombre),
        correo = validarCorreo(f.correo),
        monto = validarMonto(f.monto),
        destino = if (f.destino.isBlank()) "Selecciona a qué quieres destinar tu donación" else null,
        fecha = validarFecha(f.fechaCargo, hoyUtc),
        tarjeta = validarTarjeta(f.numeroTarjeta),
        vencimiento = validarVencimiento(f.vencimiento, ahora),
        cvv = validarCvv(f.cvv, f.numeroTarjeta),
        terminos = if (!f.aceptaTerminos) "Debes aceptar los términos y condiciones" else null
    )

    private fun validarNombre(nombre: String): String? {
        val n = nombre.trim()
        return when {
            n.isEmpty() -> "El nombre es obligatorio"
            n.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            !REGEX_NOMBRE.matches(n) -> "El nombre solo puede contener letras"
            else -> null
        }
    }

    private fun validarCorreo(correo: String): String? {
        val c = correo.trim()
        return when {
            c.isEmpty() -> "El correo es obligatorio"
            !REGEX_CORREO.matches(c) -> "Ingresa un correo válido, por ejemplo nombre@correo.cl"
            else -> null
        }
    }

    private fun validarMonto(texto: String): String? {
        val monto = texto.toIntOrNull()
        return when {
            texto.isEmpty() || monto == null -> "Ingresa el monto que quieres donar"
            monto < MONTO_MINIMO -> "El monto mínimo es ${formatoPesos(MONTO_MINIMO)}"
            monto > MONTO_MAXIMO -> "El monto máximo es ${formatoPesos(MONTO_MAXIMO)}"
            else -> null
        }
    }

    private fun validarFecha(fecha: Long?, hoyUtc: Long): String? = when {
        fecha == null -> "Selecciona la fecha del cargo"
        fecha < hoyUtc -> "La fecha no puede ser anterior a hoy"
        else -> null
    }

    private fun validarTarjeta(numero: String): String? = when {
        numero.isEmpty() -> "Ingresa el número de la tarjeta"
        numero.length < 13 -> "El número de la tarjeta está incompleto"
        !cumpleLuhn(numero) -> "El número de la tarjeta no es válido"
        else -> null
    }

    private fun validarVencimiento(texto: String, ahora: Calendar): String? {
        if (texto.isEmpty()) return "Ingresa la fecha de vencimiento"
        if (texto.length < 4) return "Usa el formato MM/AA"
        val mes = texto.substring(0, 2).toInt()
        val anio = 2000 + texto.substring(2, 4).toInt()
        val anioActual = ahora.get(Calendar.YEAR)
        val mesActual = ahora.get(Calendar.MONTH) + 1
        return when {
            mes !in 1..12 -> "El mes debe estar entre 01 y 12"
            anio < anioActual || (anio == anioActual && mes < mesActual) -> "La tarjeta está vencida"
            anio > anioActual + 20 -> "El año de vencimiento no es válido"
            else -> null
        }
    }

    private fun validarCvv(cvv: String, numeroTarjeta: String): String? {
        val largo = if (esAmex(numeroTarjeta)) 4 else 3
        return when {
            cvv.isEmpty() -> "Ingresa el código de seguridad (CVV)"
            cvv.length != largo -> "El CVV debe tener $largo dígitos"
            else -> null
        }
    }

    fun esAmex(numero: String) = numero.startsWith("34") || numero.startsWith("37")

    fun cumpleLuhn(numero: String): Boolean {
        var suma = 0
        var duplicar = false
        for (i in numero.length - 1 downTo 0) {
            var digito = numero[i] - '0'
            if (duplicar) {
                digito *= 2
                if (digito > 9) digito -= 9
            }
            suma += digito
            duplicar = !duplicar
        }
        return suma % 10 == 0
    }
}
