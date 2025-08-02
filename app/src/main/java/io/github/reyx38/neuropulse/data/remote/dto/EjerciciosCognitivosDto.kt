package io.github.reyx38.neuropulse.data.remote.dto

data class EjerciciosCognitivosDto(
    val ejercicosCognitivosId: Int? = null,
    val titulo: String,
    val descripcion: String,
    val activo: Boolean = false
)