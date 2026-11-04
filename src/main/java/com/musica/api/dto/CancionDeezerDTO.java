package com.musica.api.dto;

public record CancionDeezerDTO(
        String titulo,
        String artista,
        String album,
        String portada,
        String preview,
        String link
) {
}
