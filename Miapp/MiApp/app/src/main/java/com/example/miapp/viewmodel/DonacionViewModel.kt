package com.example.miapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miapp.data.local.AppDatabase
import com.example.miapp.data.model.Campo
import com.example.miapp.data.model.DonacionConfirmada
import com.example.miapp.data.model.DonacionErrores
import com.example.miapp.data.model.DonacionForm
import com.example.miapp.data.model.Ubicacion
import com.example.miapp.data.repository.DonacionRepository
import com.example.miapp.data.repository.UbicacionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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

    private val _mostrarSugerencias = MutableStateFlow(false)

    val sugerencias: StateFlow<List<Ubicacion>> = combine(
        _form.map { it.ubicacion }.distinctUntilChanged(),
        _mostrarSugerencias
    ) { texto, mostrar ->
        if (mostrar) UbicacionRepository.buscar(texto) else emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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

    fun actualizarUbicacion(valor: String) {
        _form.update { it.copy(ubicacion = valor.take(40)) }
        _mostrarSugerencias.value = true
    }

    fun elegirUbicacion(ubicacion: Ubicacion) {
        _form.update { it.copy(ubicacion = ubicacion.comuna) }
        _mostrarSugerencias.value = false
        tocar(Campo.Ubicacion)
    }

    fun ocultarSugerencias() {
        _mostrarSugerencias.value = false
    }

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

        val formulario = _form.value
        if (DonacionValidator.validar(formulario).hayErrores) return

        val ubicacion = UbicacionRepository.encontrarExacta(formulario.ubicacion) ?: return
        val datos = formulario.copy(ubicacion = ubicacion.comuna)

        viewModelScope.launch {
            _procesando.value = true
            _errorGuardado.value = null
            try {
                delay(1_500)                                                              

                repositorio.guardar(datos)
                _confirmacion.value = DonacionConfirmada(
                    nombre = datos.nombre.trim(),
                    monto = datos.monto.toInt(),
                    ubicacion = datos.ubicacion
                )

                _form.value = DonacionForm()
                _tocados.value = emptySet()
                _mostrarSugerencias.value = false
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
