package com.example.miapp.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miapp.data.repository.FotoPerfilRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FotoPerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = FotoPerfilRepository(application)

    private val _foto = MutableStateFlow<ImageBitmap?>(null)
    val foto: StateFlow<ImageBitmap?> = _foto.asStateFlow()

    init {

        viewModelScope.launch {
            _foto.value = withContext(Dispatchers.IO) { repositorio.cargar() }
        }
    }

    fun guardarFoto(uri: Uri) {
        viewModelScope.launch {
            val nueva = withContext(Dispatchers.IO) { repositorio.guardar(uri) }
            if (nueva != null) _foto.value = nueva
        }
    }
}
