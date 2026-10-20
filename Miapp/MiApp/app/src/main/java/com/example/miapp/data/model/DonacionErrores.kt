package com.example.miapp.data.model

enum class Campo { Nombre, Correo, Ubicacion, Monto, Fecha, Tarjeta, Vencimiento, Cvv, Terminos }

data class DonacionErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val ubicacion: String? = null,
    val monto: String? = null,
    val fecha: String? = null,
    val tarjeta: String? = null,
    val vencimiento: String? = null,
    val cvv: String? = null,
    val terminos: String? = null
) {
    val hayErrores: Boolean
        get() = listOf(nombre, correo, ubicacion, monto, fecha, tarjeta, vencimiento, cvv, terminos)
            .any { it != null }

    fun soloDe(campos: Set<Campo>) = DonacionErrores(
        nombre = nombre.takeIf { Campo.Nombre in campos },
        correo = correo.takeIf { Campo.Correo in campos },
        ubicacion = ubicacion.takeIf { Campo.Ubicacion in campos },
        monto = monto.takeIf { Campo.Monto in campos },
        fecha = fecha.takeIf { Campo.Fecha in campos },
        tarjeta = tarjeta.takeIf { Campo.Tarjeta in campos },
        vencimiento = vencimiento.takeIf { Campo.Vencimiento in campos },
        cvv = cvv.takeIf { Campo.Cvv in campos },
        terminos = terminos.takeIf { Campo.Terminos in campos }
    )
}
