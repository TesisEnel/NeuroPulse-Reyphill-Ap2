package io.github.reyx38.neuropulse.data.local.Mappers

import io.github.reyx38.neuropulse.data.local.entities.ReflexionEntity
import io.github.reyx38.neuropulse.data.remote.dto.ReflexionDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun ReflexionEntity.toDto(): ReflexionDto{
    return ReflexionDto(
        reflexionId,
        usuarioId,
        descripcion,
        estadoReflexion,
        fechaCreacion = fechaCreacion.toDateStringOrNull()
    )
}
fun ReflexionDto.toEntity(): ReflexionEntity {
    return ReflexionEntity(
        reflexionId,
        usuarioId,
        descripcion,
        estadoReflexion,
        fechaCreacion = fechaCreacion.toDateOrNull()
    )
}

fun Date?.toDateStringOrNull(): String? {
    return try {
        this?.let {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(it)
        }
    } catch (e: Exception) {
        null
    }
}

fun String?.toDateOrNull(): Date? {
    return try {
        if (this == null || this == "0001-01-01T00:00:00") null
        else SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(this)
    } catch (e: Exception) {
        null
    }
}