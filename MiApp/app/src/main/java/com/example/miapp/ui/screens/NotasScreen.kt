package com.example.miapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.miapp.data.model.CampoNota
import com.example.miapp.data.model.Nota
import com.example.miapp.ui.components.CampoFormulario
import com.example.miapp.util.formatoFechaHora
import com.example.miapp.viewmodel.NotaValidator
import com.example.miapp.viewmodel.NotasViewModel

@Composable
fun NotasScreen(
    modifier: Modifier = Modifier,
    viewModel: NotasViewModel = viewModel()
) {
    val form by viewModel.form.collectAsState()
    val errores by viewModel.errores.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val notas by viewModel.notas.collectAsState()
    val totalNotas by viewModel.totalNotas.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var porEliminar by remember { mutableStateOf<Nota?>(null) }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                Column {
                    Text("Mis notas", style = MaterialTheme.typography.headlineMedium)
                    Text("Guarda tus ideas. Quedan guardadas en el teléfono.")
                }
            }

            item {
                CampoFormulario(
                    value = form.titulo,
                    onValueChange = viewModel::actualizarTitulo,
                    label = "Título",
                    error = errores.titulo,
                    icono = Icons.Default.Edit,
                    ayuda = "Entre ${NotaValidator.TITULO_MIN} y ${NotaValidator.TITULO_MAX} caracteres",
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    onPerdioFoco = { viewModel.tocar(CampoNota.Titulo) }
                )
            }
            item {
                CampoFormulario(
                    value = form.contenido,
                    onValueChange = viewModel::actualizarContenido,
                    label = "Contenido",
                    error = errores.contenido,
                    ayuda = "${form.contenido.length}/${NotaValidator.CONTENIDO_MAX}",
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    singleLine = false,
                    minLines = 3,
                    onPerdioFoco = { viewModel.tocar(CampoNota.Contenido) }
                )
            }
            item {
                Button(
                    onClick = viewModel::guardar,
                    enabled = !guardando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (guardando) "Guardando..." else "Guardar nota")
                }
            }

            item {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = if (busqueda.isBlank()) {
                            "Notas guardadas ($totalNotas)"
                        } else {
                            "${notas.size} de $totalNotas notas"
                        },
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            item {
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = viewModel::actualizarBusqueda,
                    label = { Text("Buscar en mis notas") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (busqueda.isNotEmpty()) {
                            IconButton(onClick = { viewModel.actualizarBusqueda("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Borrar búsqueda")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            when {
                totalNotas == 0 -> item {
                    Text("Todavía no hay notas. Guarda la primera usando el formulario.")
                }

                notas.isEmpty() -> item {
                    Text("Ninguna nota coincide con \"${busqueda.trim()}\".")
                }

                else -> items(notas, key = { it.id }) { nota ->
                    TarjetaNota(
                        nota = nota,
                        onEliminar = { porEliminar = nota },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .imePadding()
                .padding(16.dp)
        )
    }

    porEliminar?.let { nota ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            title = { Text("Eliminar nota") },
            text = { Text("¿Quieres eliminar \"${nota.titulo}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminar(nota)
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
private fun TarjetaNota(
    nota: Nota,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(nota.titulo, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = formatoFechaHora(nota.creadaEn),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = nota.contenido,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar nota"
                )
            }
        }
    }
}
