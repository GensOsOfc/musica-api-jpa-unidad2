package com.musica.api.dto;

/**
 * DTO de entrada para crear o actualizar. No incluye "id": lo asigna la base.
 */
public record CancionDTO(
        String titulo,
        String artista,
        String genero,
        int anio,
        int duracionSegundos
) {
}
