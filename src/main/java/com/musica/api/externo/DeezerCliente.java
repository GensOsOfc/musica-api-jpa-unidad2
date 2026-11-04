package com.musica.api.externo;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.musica.api.excepcion.ServicioExternoException;

@Component
public class DeezerCliente {

    private final RestClient restClient;

    public DeezerCliente(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://api.deezer.com").build();
    }

    public Optional<DeezerRespuesta.DeezerTrack> buscar(String titulo, String artista) {
        try {
            DeezerRespuesta respuesta = restClient.get()
                    .uri(b -> b.path("/search").queryParam("q", titulo + " " + artista).build())
                    .retrieve()
                    .body(DeezerRespuesta.class);

            if (respuesta == null || respuesta.data() == null || respuesta.data().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(respuesta.data().get(0));
        } catch (RestClientException e) {
            throw new ServicioExternoException("No se pudo consultar la API de Deezer: " + e.getMessage());
        }
    }
}
