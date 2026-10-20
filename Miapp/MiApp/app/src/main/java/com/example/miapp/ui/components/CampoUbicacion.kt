package com.example.miapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.miapp.data.model.Ubicacion

@Composable
fun CampoUbicacion(
    value: String,
    onValueChange: (String) -> Unit,
    sugerencias: List<Ubicacion>,
    onSugerenciaElegida: (Ubicacion) -> Unit,
    error: String?,
    onPerdioFoco: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        CampoFormulario(
            value = value,
            onValueChange = onValueChange,
            label = "Desde dónde donas",
            error = error,
            icono = Icons.Default.LocationOn,
            ayuda = "Escribe tu ciudad o comuna y elige una de la lista",
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            onPerdioFoco = onPerdioFoco
        )

        AnimatedVisibility(
            visible = sugerencias.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                sugerencias.forEachIndexed { indice, ubicacion ->
                    if (indice > 0) HorizontalDivider()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSugerenciaElegida(ubicacion) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(ubicacion.comuna)
                        Text(
                            text = ubicacion.region,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
