package com.example.miapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miapp.data.local.AppDatabase
import com.example.miapp.data.model.CampoNota
import com.example.miapp.data.model.Nota
import com.example.miapp.data.model.NotaErrores
import com.example.miapp.data.model.NotaForm
import com.example.miapp.data.repository.NotaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class NotasViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio =
        NotaRepository(AppDatabase.getInstance(application).notaDao())

    private val _form = MutableStateFlow(NotaForm())
    val form: StateFlow<NotaForm> = _form.asStateFlow()

    private val _tocados = MutableStateFlow<Set<CampoNota>>(emptySet())

    val errores: StateFlow<NotaErrores> = combine(_form, _tocados) { datos, tocados ->
        NotaValidator.validar(datos).soloDe(tocados)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NotaErrores())

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    val totalNotas: StateFlow<Int> = repositorio.notas
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val notas: StateFlow<List<Nota>> = combine(repositorio.notas, _busqueda) { lista, texto ->
        val consulta = texto.trim()
        if (consulta.isEmpty()) {
            lista
        } else {
            lista.filter {
                it.titulo.contains(consulta, ignoreCase = true) ||
                        it.contenido.contains(consulta, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun actualizarTitulo(valor: String) =
        _form.update { it.copy(titulo = valor.take(NotaValidator.TITULO_MAX)) }

    fun actualizarContenido(valor: String) =
        _form.update { it.copy(contenido = valor.take(NotaValidator.CONTENIDO_MAX)) }

    fun tocar(campo: CampoNota) = _tocados.update { it + campo }

    fun actualizarBusqueda(valor: String) {
        _busqueda.value = valor.take(50)
    }

    fun guardar() {
        if (_guardando.value) return

        _tocados.value = CampoNota.entries.toSet()

        val datos = _form.value
        if (NotaValidator.validar(datos).hayErrores) return

        viewModelScope.launch {
            _guardando.value = true
            try {
                repositorio.guardar(datos)
                _form.value = NotaForm()
                _tocados.value = emptySet()
                _mensaje.value = "Nota guardada correctamente."
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _mensaje.value = "No se pudo guardar la nota. Inténtalo de nuevo."
            } finally {
                _guardando.value = false
            }
        }
    }

    fun eliminar(nota: Nota) {
        viewModelScope.launch {
            try {
                repositorio.eliminar(nota)
                _mensaje.value = "Nota eliminada."
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _mensaje.value = "No se pudo eliminar la nota."
            }
        }
    }

    fun mensajeMostrado() {
        _mensaje.value = null
    }
}
