package com.example.miapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miapp.data.local.AppDatabase
import com.example.miapp.data.model.Campo
import com.example.miapp.data.model.DonacionConfirmada
import com.example.miapp.data.model.DonacionErrores
import com.example.miapp.data.model.DonacionForm
import com.example.miapp.data.repository.DonacionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class DonacionViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio =
        DonacionRepository(AppDatabase.getInstance(application).donacionDao())

    private val _form = MutableStateFlow(DonacionForm())
    val form: StateFlow<DonacionForm> = _form.asStateFlow()

    private val _tocados = MutableStateFlow<Set<Campo>>(emptySet())

    val errores: StateFlow<DonacionErrores> = combine(_form, _tocados) { datos, tocados ->
        DonacionValidator.validar(datos).soloDe(tocados)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DonacionErrores())

    private val _procesando = MutableStateFlow(false)
    val procesando: StateFlow<Boolean> = _procesando.asStateFlow()

    private val _errorGuardado = MutableStateFlow<String?>(null)
    val errorGuardado: StateFlow<String?> = _errorGuardado.asStateFlow()

    private val _confirmacion = MutableStateFlow<DonacionConfirmada?>(null)
    val confirmacion: StateFlow<DonacionConfirmada?> = _confirmacion.asStateFlow()

    fun actualizarNombre(valor: String) = _form.update { it.copy(nombre = valor.take(60)) }

    fun actualizarCorreo(valor: String) = _form.update { it.copy(correo = valor.take(80)) }

    fun actualizarMonto(valor: String) =
        _form.update { it.copy(monto = valor.filter(Char::isDigit).take(7)) }

    fun actualizarDestino(valor: String) {
        _form.update { it.copy(destino = valor) }
        tocar(Campo.Destino)
    }

    fun actualizarMensual(valor: Boolean) = _form.update { it.copy(mensual = valor) }

    fun actualizarFecha(valor: Long?) {
        _form.update { it.copy(fechaCargo = valor) }
        tocar(Campo.Fecha)
    }

    fun actualizarTarjeta(valor: String) =
        _form.update { it.copy(numeroTarjeta = valor.filter(Char::isDigit).take(19)) }

    fun actualizarVencimiento(valor: String) =
        _form.update { it.copy(vencimiento = valor.filter(Char::isDigit).take(4)) }

    fun actualizarCvv(valor: String) =
        _form.update { it.copy(cvv = valor.filter(Char::isDigit).take(4)) }

    fun actualizarTerminos(valor: Boolean) {
        _form.update { it.copy(aceptaTerminos = valor) }
        tocar(Campo.Terminos)
    }

    fun tocar(campo: Campo) = _tocados.update { it + campo }

    fun donar() {
        if (_procesando.value) return

        _tocados.value = Campo.entries.toSet()

        val datos = _form.value
        if (DonacionValidator.validar(datos).hayErrores) return

        viewModelScope.launch {
            _procesando.value = true
            _errorGuardado.value = null
            try {
                delay(1_500)                                                              

                repositorio.guardar(datos)
                _confirmacion.value = DonacionConfirmada(
                    nombre = datos.nombre.trim(),
                    monto = datos.monto.toInt(),
                    destino = datos.destino,
                    mensual = datos.mensual
                )

                _form.value = DonacionForm()
                _tocados.value = emptySet()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _errorGuardado.value = "No se pudo guardar la donación. Inténtalo de nuevo."
            } finally {
                _procesando.value = false
            }
        }
    }

    fun cerrarConfirmacion() {
        _confirmacion.value = null
    }
}
