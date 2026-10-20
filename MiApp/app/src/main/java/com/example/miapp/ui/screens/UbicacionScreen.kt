package com.example.miapp.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private const val LATITUD = -33.45407486081605
private const val LONGITUD = -70.62542812369253

@Composable
fun UbicacionScreen(modifier: Modifier = Modifier) {
    val contexto = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            16.dp,
            Alignment.CenterVertically
        )
    ) {
        Text(
            text = "Ubicación de CORPALIV",
            style = MaterialTheme.typography.headlineSmall
        )

        Text("Presiona el botón para abrir la ubicación.")

        Button(
            onClick = {
                val enlace = Uri.parse(
                    "https://www.google.com/maps/search/"
                ).buildUpon()
                    .appendQueryParameter("api", "1")
                    .appendQueryParameter(
                        "query",
                        "$LATITUD,$LONGITUD"
                    )
                    .build()

                val intent = Intent(Intent.ACTION_VIEW, enlace)

                try {
                    contexto.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        contexto,
                        "No hay una aplicación para abrir el mapa.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        ) {
            Text("Ver en Google Maps")
        }
    }
}
