package com.example.miapp.data.model

data class DonacionForm(
    val nombre: String = "",
    val correo: String = "",
    val ubicacion: String = "",                                                    
    val monto: String = "",
    val fechaCargo: Long? = null,                                                            
    val numeroTarjeta: String = "",
    val vencimiento: String = "",                                   
    val cvv: String = "",
    val aceptaTerminos: Boolean = false
)

data class DonacionConfirmada(
    val nombre: String,
    val monto: Int,
    val ubicacion: String
)
