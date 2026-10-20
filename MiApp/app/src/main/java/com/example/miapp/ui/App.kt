package com.example.miapp.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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

private const val DURACION_TRANSICION_MS = 320

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

            AnimatedContent(
                targetState = pantallaActual,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                transitionSpec = {
                    val haciaAdelante = targetState.ordinal > initialState.ordinal
                    val sentido = if (haciaAdelante) 1 else -1

                    (fadeIn(tween(DURACION_TRANSICION_MS, delayMillis = 60)) +
                            slideInHorizontally(tween(DURACION_TRANSICION_MS)) { sentido * it / 4 }) togetherWith
                            (fadeOut(tween(DURACION_TRANSICION_MS / 2)) +
                                    slideOutHorizontally(tween(DURACION_TRANSICION_MS)) { -sentido * it / 4 })
                },
                label = "transicionPantallas"
            ) { pantalla ->

                when (pantalla) {
                    Pantalla.Inicio -> InicioScreen()
                    Pantalla.Nosotros -> NosotrosScreen()
                    Pantalla.Productos -> ProductosScreen()
                    Pantalla.Donaciones -> DonacionesScreen()
                    Pantalla.Historial -> HistorialScreen()
                    Pantalla.Notas -> NotasScreen()
                }
            }
        }
    }
}
