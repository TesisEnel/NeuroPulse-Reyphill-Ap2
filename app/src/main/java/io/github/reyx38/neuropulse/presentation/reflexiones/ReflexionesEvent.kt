package io.github.reyx38.neuropulse.presentation.reflexiones


sealed interface ReflexionesEvent {
    data class UsuarioChange (val usuarioId : Int): ReflexionesEvent
    data class DescripcionChange (val descripcion: String): ReflexionesEvent
    data class EstadoReflexion (val estadoReflexion: String): ReflexionesEvent

    data object Save: ReflexionesEvent
    data object New: ReflexionesEvent
    data class Delete(val reflexionid: Int): ReflexionesEvent
}

