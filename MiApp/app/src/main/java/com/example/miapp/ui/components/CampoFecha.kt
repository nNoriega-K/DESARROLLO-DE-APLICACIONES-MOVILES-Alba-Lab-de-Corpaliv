package com.example.miapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.miapp.util.formatoFechaUtc
import com.example.miapp.util.hoyUtcMillis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFecha(
    fechaMillis: Long?,
    label: String,
    error: String?,
    onFechaSeleccionada: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    ayuda: String? = null
) {
    var mostrarCalendario by remember { mutableStateOf(false) }
    val texto = fechaMillis?.let { formatoFechaUtc(it) } ?: ""

    Box(modifier) {
        CampoFormulario(
            value = texto,
            onValueChange = {},
            label = label,
            error = error,
            icono = Icons.Default.DateRange,
            ayuda = ayuda,
            readOnly = true
        )

        Box(
            Modifier
                .matchParentSize()
                .clickable(onClickLabel = "Elegir fecha") { mostrarCalendario = true }
        )
    }

    if (mostrarCalendario) {
        val hoy = remember { hoyUtcMillis() }
        val estado = rememberDatePickerState(
            initialSelectedDateMillis = fechaMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis >= hoy
            }
        )
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    onFechaSeleccionada(estado.selectedDateMillis)
                    mostrarCalendario = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estado)
        }
    }
}
