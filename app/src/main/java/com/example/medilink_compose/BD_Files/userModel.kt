package com.example.medilink_compose.BD_Files

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UsuarioViewModel : ViewModel() {
    private val _usuarioLogueado = MutableStateFlow<String?>(null)
    val usuarioLogueado = _usuarioLogueado.asStateFlow()

    fun setUsuario(nombre: String) {
        _usuarioLogueado.value = nombre
    }
}
