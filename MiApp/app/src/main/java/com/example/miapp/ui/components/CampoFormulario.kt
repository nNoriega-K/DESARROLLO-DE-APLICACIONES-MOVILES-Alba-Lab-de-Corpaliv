package com.example.miapp.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun MensajeError(texto: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Text(
            text = texto,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ErrorAnimado(error: String?, modifier: Modifier = Modifier) {
    AnimatedContent(targetState = error, modifier = modifier, label = "errorAnimado") { mensaje ->
        if (mensaje != null) MensajeError(mensaje)
    }
}

@Composable
fun TextoApoyo(error: String?, ayuda: String?) {
    AnimatedContent(targetState = error, label = "textoApoyo") { mensaje ->
        if (mensaje != null) MensajeError(mensaje) else Text(ayuda ?: " ")
    }
}

@Composable
fun CampoFormulario(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    ayuda: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    minLines: Int = 1,
    readOnly: Boolean = false,
    onPerdioFoco: () -> Unit = {}
) {
    var tuvoFoco by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = if (icono != null) {
            { Icon(imageVector = icono, contentDescription = null) }
        } else null,
        supportingText = { TextoApoyo(error = error, ayuda = ayuda) },
        isError = error != null,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        singleLine = singleLine,
        minLines = minLines,
        readOnly = readOnly,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { estado ->
                if (estado.isFocused) {
                    tuvoFoco = true
                } else if (tuvoFoco) {
                    tuvoFoco = false
                    onPerdioFoco()                                                  
                }
            }
    )
}
