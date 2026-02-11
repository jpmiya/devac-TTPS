package org.example.devac.dto;

import java.time.LocalDate;

public record AvistamientoListDto(
        Long id,
        String fecha,
        String coordenadas,
        String comentario,
        Long usuarioId,
        String usuarioNombre,
        Long mascotaId,
        String mascotaNombre,
        String fotoUrl
) {}
