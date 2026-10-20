package com.example.miapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.miapp.data.model.Donacion
import com.example.miapp.ui.components.ContenidoCentrado
import com.example.miapp.util.formatoFechaUtc
import com.example.miapp.util.formatoPesos
import com.example.miapp.viewmodel.HistorialViewModel

@Composable
fun HistorialScreen(
    modifier: Modifier = Modifier,
    viewModel: HistorialViewModel = viewModel()
) {
    val donaciones by viewModel.donaciones.collectAsState()
    var porEliminar by remember { mutableStateOf<Donacion?>(null) }

    ContenidoCentrado(modifier) {
        if (donaciones.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Aún no hay donaciones",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text("Cuando hagas una, aparecerá aquí.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column {
                        Text("Historial", style = MaterialTheme.typography.headlineMedium)
                        Text(
                            "${donaciones.size} donaciones · Total " +
                                    formatoPesos(donaciones.sumOf { it.monto })
                        )
                    }
                }
                items(donaciones, key = { it.id }) { donacion ->
                    TarjetaDonacion(
                        donacion = donacion,
                        onEliminar = { porEliminar = donacion },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }

    porEliminar?.let { donacion ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            title = { Text("Eliminar donación") },
            text = { Text("¿Quieres eliminar la donación de ${formatoPesos(donacion.monto)}?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminar(donacion)
                    porEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { porEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun TarjetaDonacion(
    donacion: Donacion,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatoPesos(donacion.monto),
                    style = MaterialTheme.typography.titleLarge
                )
                Text("Desde ${donacion.ubicacion}")
                Text(
                    text = "Donante: ${donacion.nombre}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Cargo: ${formatoFechaUtc(donacion.fechaCargo)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar donación"
                )
            }
        }
    }
}
