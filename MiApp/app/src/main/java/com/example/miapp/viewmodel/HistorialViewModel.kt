package com.example.miapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miapp.data.local.AppDatabase
import com.example.miapp.data.model.Donacion
import com.example.miapp.data.repository.DonacionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistorialViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio =
        DonacionRepository(AppDatabase.getInstance(application).donacionDao())

    val donaciones: StateFlow<List<Donacion>> = repositorio.donaciones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun eliminar(donacion: Donacion) {
        viewModelScope.launch { repositorio.eliminar(donacion) }
    }
}
