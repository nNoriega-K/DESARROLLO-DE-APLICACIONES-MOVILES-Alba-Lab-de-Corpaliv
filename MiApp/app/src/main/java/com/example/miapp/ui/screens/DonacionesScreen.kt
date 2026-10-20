package com.example.miapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.miapp.data.model.Campo
import com.example.miapp.ui.components.CampoFecha
import com.example.miapp.ui.components.CampoFormulario
import com.example.miapp.ui.components.CampoUbicacion
import com.example.miapp.ui.components.ErrorAnimado
import com.example.miapp.ui.components.TarjetaVisualTransformation
import com.example.miapp.ui.components.VencimientoVisualTransformation
import com.example.miapp.viewmodel.DonacionValidator
import com.example.miapp.util.formatoPesos
import com.example.miapp.viewmodel.DonacionViewModel

private val MONTOS_RAPIDOS = listOf(5_000, 10_000, 20_000)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonacionesScreen(
    modifier: Modifier = Modifier,
    viewModel: DonacionViewModel = viewModel()
) {
    val form by viewModel.form.collectAsState()
    val errores by viewModel.errores.collectAsState()
    val procesando by viewModel.procesando.collectAsState()
    val confirmacion by viewModel.confirmacion.collectAsState()
    val sugerencias by viewModel.sugerencias.collectAsState()
    val errorGuardado by viewModel.errorGuardado.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Donaciones", style = MaterialTheme.typography.headlineMedium)
        Text("Tu aporte apoya la inclusión laboral, la formación y el apoyo a familias.")

        Spacer(Modifier.height(8.dp))
        Text("Tus datos", style = MaterialTheme.typography.titleMedium)

        CampoFormulario(
            value = form.nombre,
            onValueChange = viewModel::actualizarNombre,
            label = "Nombre completo",
            error = errores.nombre,
            icono = Icons.Default.Person,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            onPerdioFoco = { viewModel.tocar(Campo.Nombre) }
        )

        CampoFormulario(
            value = form.correo,
            onValueChange = viewModel::actualizarCorreo,
            label = "Correo electrónico",
            error = errores.correo,
            icono = Icons.Default.Email,
            ayuda = "Aquí enviaremos tu comprobante",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            onPerdioFoco = { viewModel.tocar(Campo.Correo) }
        )

        CampoUbicacion(
            value = form.ubicacion,
            onValueChange = viewModel::actualizarUbicacion,
            sugerencias = sugerencias,
            onSugerenciaElegida = viewModel::elegirUbicacion,
            error = errores.ubicacion,
            onPerdioFoco = {
                viewModel.tocar(Campo.Ubicacion)
                viewModel.ocultarSugerencias()
            }
        )

        Spacer(Modifier.height(8.dp))
        Text("Tu donación", style = MaterialTheme.typography.titleMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MONTOS_RAPIDOS.forEach { monto ->
                FilterChip(
                    selected = form.monto == monto.toString(),
                    onClick = {
                        viewModel.actualizarMonto(monto.toString())
                        viewModel.tocar(Campo.Monto)
                    },
                    label = { Text(formatoPesos(monto)) }
                )
            }
        }

        CampoFormulario(
            value = form.monto,
            onValueChange = viewModel::actualizarMonto,
            label = "Monto a donar (CLP)",
            error = errores.monto,
            ayuda = "Entre ${formatoPesos(DonacionValidator.MONTO_MINIMO)} y " +
                    formatoPesos(DonacionValidator.MONTO_MAXIMO),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            onPerdioFoco = { viewModel.tocar(Campo.Monto) }
        )

        CampoFecha(
            fechaMillis = form.fechaCargo,
            label = "Fecha del cargo",
            error = errores.fecha,
            onFechaSeleccionada = viewModel::actualizarFecha,
            ayuda = "Día en que se realiza el cargo"
        )

        Spacer(Modifier.height(8.dp))
        Text("Datos de la tarjeta", style = MaterialTheme.typography.titleMedium)
        Text(
            "Simulación: los datos de la tarjeta no se guardan ni se envían a ningún lugar.",
            style = MaterialTheme.typography.bodySmall
        )

        CampoFormulario(
            value = form.numeroTarjeta,
            onValueChange = viewModel::actualizarTarjeta,
            label = "Número de tarjeta",
            error = errores.tarjeta,
            visualTransformation = TarjetaVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            onPerdioFoco = { viewModel.tocar(Campo.Tarjeta) }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CampoFormulario(
                value = form.vencimiento,
                onValueChange = viewModel::actualizarVencimiento,
                label = "Vence (MM/AA)",
                error = errores.vencimiento,
                visualTransformation = VencimientoVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.weight(1f),
                onPerdioFoco = { viewModel.tocar(Campo.Vencimiento) }
            )
            CampoFormulario(
                value = form.cvv,
                onValueChange = viewModel::actualizarCvv,
                label = "CVV",
                error = errores.cvv,
                icono = Icons.Default.Lock,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.weight(1f),
                onPerdioFoco = { viewModel.tocar(Campo.Cvv) }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = form.aceptaTerminos,
                    role = Role.Checkbox,
                    onValueChange = viewModel::actualizarTerminos
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = form.aceptaTerminos, onCheckedChange = null)
            Spacer(Modifier.width(8.dp))
            Text("Acepto los términos y condiciones")
        }
        ErrorAnimado(error = errores.terminos, modifier = Modifier.padding(start = 4.dp))

        Spacer(Modifier.height(8.dp))
        ErrorAnimado(error = errorGuardado)
        Button(
            onClick = viewModel::donar,
            enabled = !procesando,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (procesando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = LocalContentColor.current
                )
                Spacer(Modifier.width(10.dp))
                Text("Procesando...")
            } else {
                Icon(Icons.Default.Favorite, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Donar")
            }
        }
    }

    confirmacion?.let { c ->
        AlertDialog(
            onDismissRequest = viewModel::cerrarConfirmacion,
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("¡Gracias por tu donación!") },
            text = {
                Text(
                    "${c.nombre}, registramos tu donación de ${formatoPesos(c.monto)} " +
                            "desde ${c.ubicacion}." +
                            "\n\nEsto es una simulación: no se realizó ningún cobro."
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::cerrarConfirmacion) { Text("Listo") }
            }
        )
    }
}
