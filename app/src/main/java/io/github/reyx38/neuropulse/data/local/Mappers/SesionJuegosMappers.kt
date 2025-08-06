package io.github.reyx38.neuropulse.data.local.Mappers

import io.github.reyx38.neuropulse.data.local.entities.SesionJuegoEntity
import io.github.reyx38.neuropulse.data.remote.dto.SesionJuegosDto

fun SesionJuegoEntity.toDto(): SesionJuegosDto {
    return SesionJuegosDto(
        sesionJuegoId = sesionJuegoId,
        usuarioId = usuarioId,
        ejercicioCognitivoId = ejercicioId,
        fechaRealizacion = fechaRealizacion,
        completado = completado,
        puntuacion = puntuacion
    )
}

fun SesionJuegosDto.toEntity(): SesionJuegoEntity {
    return SesionJuegoEntity (
        sesionJuegoId = sesionJuegoId,
        usuarioId = usuarioId,
        ejercicioId = ejercicioCognitivoId,
        completado = completado,
        fechaRealizacion = fechaRealizacion,
        puntuacion = puntuacion
    )
}