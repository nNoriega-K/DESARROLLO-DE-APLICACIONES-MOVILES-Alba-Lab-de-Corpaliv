package com.example.miapp.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.escalaAlPresionar(
    interactionSource: MutableInteractionSource,
    escalaPresionada: Float = 0.96f
): Modifier = composed {
    val presionado by interactionSource.collectIsPressedAsState()
    val escala by animateFloatAsState(
        targetValue = if (presionado) escalaPresionada else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "escalaAlPresionar"
    )
    graphicsLayer {
        scaleX = escala
        scaleY = escala
    }
}
