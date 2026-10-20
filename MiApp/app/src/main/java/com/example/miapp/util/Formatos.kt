package com.example.miapp.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val LOCALE_CL = Locale("es", "CL")

fun formatoPesos(monto: Int): String = "\$" + NumberFormat.getInstance(LOCALE_CL).format(monto)

fun formatoFechaUtc(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy", LOCALE_CL).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date(millis))

fun hoyUtcMillis(): Long {
    val local = Calendar.getInstance()
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(
            local.get(Calendar.YEAR),
            local.get(Calendar.MONTH),
            local.get(Calendar.DAY_OF_MONTH)
        )
    }.timeInMillis
}

fun formatoFechaHora(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", LOCALE_CL).format(Date(millis))
