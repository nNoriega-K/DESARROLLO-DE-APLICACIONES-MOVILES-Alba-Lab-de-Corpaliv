package com.example.miapp.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    titulo: String,
    onMenuClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Abrir menu"
                )
            }
        },
        title = {

            AnimatedContent(
                targetState = titulo,
                transitionSpec = {
                    (fadeIn(tween(250, delayMillis = 80)) +
                            slideInVertically(tween(250)) { it / 2 }) togetherWith
                            (fadeOut(tween(120)) +
                                    slideOutVertically(tween(250)) { -it / 2 })
                },
                label = "tituloBarra"
            ) { texto ->
                Text(texto)
            }
        }
    )
}
