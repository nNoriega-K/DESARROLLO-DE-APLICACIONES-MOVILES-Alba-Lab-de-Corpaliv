package com.example.miapp.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.miapp.viewmodel.FotoPerfilViewModel

@Composable
fun FotoPerfil(
    modifier: Modifier = Modifier,
    tamano: Dp = 96.dp,
    viewModel: FotoPerfilViewModel = viewModel()
) {
    val context = LocalContext.current
    val foto by viewModel.foto.collectAsState()

    var menuAbierto by remember { mutableStateOf(false) }
    var mostrarAyudaPermiso by remember { mutableStateOf(false) }

    var uriCamara by rememberSaveable { mutableStateOf<Uri?>(null) }

    val galeria = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) viewModel.guardarFoto(uri)
    }

    val camara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        val uri = uriCamara
        if (exito && uri != null) {
            viewModel.guardarFotoDeCamara(uri)
        } else {
            viewModel.descartarFotosTemporales()
        }
        uriCamara = null
    }

    fun abrirCamara() {
        try {
            val uri = viewModel.crearUriCamara()
            uriCamara = uri
            camara.launch(uri)
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir la cámara", Toast.LENGTH_LONG).show()
        }
    }

    val permisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) abrirCamara() else mostrarAyudaPermiso = true
    }

    fun tomarFoto() {
        val concedido = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (concedido) abrirCamara() else permisoCamara.launch(Manifest.permission.CAMERA)
    }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(tamano)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClickLabel = "Cambiar foto de perfil") { menuAbierto = true },
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

        DropdownMenu(
            expanded = menuAbierto,
            onDismissRequest = { menuAbierto = false }
        ) {
            DropdownMenuItem(
                text = { Text("Tomar foto") },
                leadingIcon = { Icon(Icons.Default.CameraAlt, contentDescription = null) },
                onClick = {
                    menuAbierto = false
                    tomarFoto()
                }
            )
            DropdownMenuItem(
                text = { Text("Elegir de la galería") },
                leadingIcon = { Icon(Icons.Default.Collections, contentDescription = null) },
                onClick = {
                    menuAbierto = false
                    galeria.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        }
    }

    if (mostrarAyudaPermiso) {
        AlertDialog(
            onDismissRequest = { mostrarAyudaPermiso = false },
            title = { Text("Permiso de cámara") },
            text = {
                Text(
                    "Usamos la cámara solo para tomar tu foto de perfil. " +
                            "Si quieres usarla, activa el permiso desde Ajustes. " +
                            "También puedes elegir una foto de la galería."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    mostrarAyudaPermiso = false
                    abrirAjustesDeLaApp(context)
                }) { Text("Abrir ajustes") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarAyudaPermiso = false }) { Text("Ahora no") }
            }
        )
    }
}

private fun abrirAjustesDeLaApp(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    context.startActivity(intent)
}
