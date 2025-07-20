package io.github.reyx38.neuropulse.presentation.reflexiones


sealed interface ReflexionesEvent {
    data class usuarioChange (val usuarioId : Int): ReflexionesEvent
    data class descripcionChange (val descripcion: String): ReflexionesEvent
    data class estadoReflexion (val estadoReflexion: String): ReflexionesEvent

    data object Save: ReflexionesEvent
    data object New: ReflexionesEvent
    data object Delete: ReflexionesEvent
}

