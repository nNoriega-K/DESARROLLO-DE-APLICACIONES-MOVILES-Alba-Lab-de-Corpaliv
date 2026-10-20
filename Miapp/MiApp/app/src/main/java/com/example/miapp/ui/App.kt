package com.example.miapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.miapp.ui.components.AppDrawer
import com.example.miapp.ui.components.AppTopBar
import com.example.miapp.ui.screens.DonacionesScreen
import com.example.miapp.ui.screens.HistorialScreen
import com.example.miapp.ui.screens.InicioScreen
import com.example.miapp.ui.screens.NosotrosScreen
import com.example.miapp.ui.screens.NotasScreen
import com.example.miapp.ui.screens.ProductosScreen

@Composable
fun App() {

    var pantallaActual by rememberSaveable { mutableStateOf(Pantalla.Inicio) }

    AppDrawer(
        pantallaActual = pantallaActual,
        onPantallaSeleccionada = { pantallaActual = it }
    ) { abrirMenu ->
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                AppTopBar(
                    titulo = pantallaActual.titulo,
                    onMenuClick = abrirMenu
                )
            }
        ) { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            when (pantallaActual) {
                Pantalla.Inicio -> InicioScreen(modifier)
                Pantalla.Nosotros -> NosotrosScreen(modifier)
                Pantalla.Productos -> ProductosScreen(modifier)
                Pantalla.Donaciones -> DonacionesScreen(modifier)
                Pantalla.Historial -> HistorialScreen(modifier)
                Pantalla.Notas -> NotasScreen(modifier)
            }
        }
    }
}
