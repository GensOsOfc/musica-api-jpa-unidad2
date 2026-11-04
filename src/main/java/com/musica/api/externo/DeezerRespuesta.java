package com.musica.api.externo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeezerRespuesta(List<DeezerTrack> data) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DeezerTrack(
            String title,
            String link,
            String preview,
            DeezerArtista artist,
            DeezerAlbum album
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DeezerArtista(String name) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DeezerAlbum(
            String title,
            @JsonProperty("cover_medium") String coverMedium
    ) {
    }
}
