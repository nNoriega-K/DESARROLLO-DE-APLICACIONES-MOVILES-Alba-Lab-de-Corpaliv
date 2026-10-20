package com.example.miapp.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class TarjetaVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        val formateado = buildString {
            original.forEachIndexed { i, c ->
                if (i > 0 && i % 4 == 0) append(' ')
                append(c)
            }
        }
        val mapa = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                offset + (offset - 1).coerceAtLeast(0) / 4

            override fun transformedToOriginal(offset: Int): Int =
                offset - offset / 5
        }
        return TransformedText(AnnotatedString(formateado), mapa)
    }
}

class VencimientoVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        val formateado = if (original.length > 2) {
            original.substring(0, 2) + "/" + original.substring(2)
        } else {
            original
        }
        val mapa = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                if (offset <= 2) offset else offset + 1

            override fun transformedToOriginal(offset: Int): Int =
                if (offset <= 2) offset else offset - 1
        }
        return TransformedText(AnnotatedString(formateado), mapa)
    }
}
