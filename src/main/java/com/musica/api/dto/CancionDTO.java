package com.musica.api.dto;

/**
 * DTO (Data Transfer Object) de entrada para crear o actualizar una canción.
 *
 * Define exactamente la forma del JSON que el cliente debe enviar en el
 * cuerpo de la petición. No incluye el campo "id" porque ese lo asigna la
 * base de datos al persistir la entidad.
 *
 * Se usa un "record" de Java porque es inmutable y conciso:
 * constructor, equals, hashCode y toString vienen incluidos.
 */
public record CancionDTO(
        String titulo,
        String artista,
        String genero,
        int anio,
        int duracionSegundos
) {
}
