package com.example.miapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.miapp.R
import com.example.miapp.ui.Pantalla
import kotlinx.coroutines.launch

private const val ALTURA_COMPACTA_DP = 500

@Composable
fun AppDrawer(
    pantallaActual: Pantalla,
    onPantallaSeleccionada: (Pantalla) -> Unit,
    contenido: @Composable (abrirMenu: () -> Unit) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {

                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    CabeceraMenu()

                    Pantalla.entries.forEach { pantalla ->
                        NavigationDrawerItem(
                            label = { Text(pantalla.titulo) },
                            selected = pantallaActual == pantalla,
                            onClick = {
                                onPantallaSeleccionada(pantalla)
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        }
    ) {
        contenido {
            scope.launch { drawerState.open() }
        }
    }
}

@Composable
private fun CabeceraMenu() {
    val compacta = LocalConfiguration.current.screenHeightDp < ALTURA_COMPACTA_DP

    if (compacta) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FotoPerfil(tamano = 64.dp)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LogoYNombre(tamanoLogo = 32)
                Text(
                    text = "Toca la foto para cambiarla",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FotoPerfil()
            Text(
                text = "Toca la foto para cambiarla",
                style = MaterialTheme.typography.bodySmall
            )
            LogoYNombre(tamanoLogo = 40)
        }
    }
}

@Composable
private fun LogoYNombre(tamanoLogo: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.logo_voluntario),
            contentDescription = "Logo",
            modifier = Modifier.size(tamanoLogo.dp)
        )
        Text("CORPALIV")
    }
}
