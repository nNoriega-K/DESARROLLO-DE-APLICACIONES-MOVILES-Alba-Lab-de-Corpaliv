package com.example.miapp.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private const val NOMBRE_ARCHIVO = "foto_perfil.jpg"

@Composable
fun FotoPerfil(
    modifier: Modifier = Modifier,
    tamano: Dp = 96.dp
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var foto by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(Unit) {
        foto = withContext(Dispatchers.IO) { cargarFoto(context) }
    }

    val selectorDeImagen = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val nueva = withContext(Dispatchers.IO) { guardarFoto(context, uri) }
                if (nueva != null) foto = nueva
            }
        }
    }

    Box(
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable {
                selectorDeImagen.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        val imagen = foto
        if (imagen != null) {
            Image(
                bitmap = imagen,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(tamano)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Elegir foto de perfil",
                modifier = Modifier.size(tamano / 2),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun guardarFoto(context: Context, uri: Uri): ImageBitmap? {
    return try {
        val archivo = File(context.filesDir, NOMBRE_ARCHIVO)
        val entrada = context.contentResolver.openInputStream(uri) ?: return null
        entrada.use { origen -> archivo.outputStream().use { destino -> origen.copyTo(destino) } }
        decodificar(archivo)
    } catch (e: Exception) {
        null
    }
}

private fun cargarFoto(context: Context): ImageBitmap? {
    val archivo = File(context.filesDir, NOMBRE_ARCHIVO)
    return if (archivo.exists()) decodificar(archivo) else null
}

private fun decodificar(archivo: File): ImageBitmap? {
    return try {
        val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(archivo.path, medidas)

        var reduccion = 1
        while (medidas.outWidth / reduccion > 600 || medidas.outHeight / reduccion > 600) {
            reduccion *= 2
        }
        val opciones = BitmapFactory.Options().apply { inSampleSize = reduccion }
        val bitmap = BitmapFactory.decodeFile(archivo.path, opciones) ?: return null

        val grados = when (
            ExifInterface(archivo.path).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        ) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        val resultado = if (grados != 0f) {
            val matriz = Matrix().apply { postRotate(grados) }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
        } else {
            bitmap
        }
        resultado.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}
