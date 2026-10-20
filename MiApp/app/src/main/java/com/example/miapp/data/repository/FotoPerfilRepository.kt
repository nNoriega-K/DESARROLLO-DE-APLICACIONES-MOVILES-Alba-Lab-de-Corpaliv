package com.example.miapp.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File

class FotoPerfilRepository(context: Context) {

    private val appContext = context.applicationContext
    private val archivo = File(appContext.filesDir, "foto_perfil.jpg")

    fun cargar(): ImageBitmap? = if (archivo.exists()) decodificar(archivo) else null

    fun guardar(uri: Uri): ImageBitmap? {
        return try {
            val entrada = appContext.contentResolver.openInputStream(uri) ?: return null
            entrada.use { origen ->
                archivo.outputStream().use { destino -> origen.copyTo(destino) }
            }
            decodificar(archivo)
        } catch (e: Exception) {
            null
        }
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
}
