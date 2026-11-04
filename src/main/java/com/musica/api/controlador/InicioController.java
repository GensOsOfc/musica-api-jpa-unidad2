package com.musica.api.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InicioController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> inicio() {
        List<Map<String, String>> endpoints = List.of(
                Map.of("metodo", "GET", "ruta", "/api/canciones", "descripcion", "Lista todas las canciones"),
                Map.of("metodo", "GET", "ruta", "/api/canciones/generos", "descripcion", "Géneros disponibles"),
                Map.of("metodo", "GET", "ruta", "/api/canciones/{id}", "descripcion", "Canción por id"),
                Map.of("metodo", "GET", "ruta", "/api/canciones/buscar?genero=rock", "descripcion", "Filtra por género"),
                Map.of("metodo", "POST", "ruta", "/api/canciones", "descripcion", "Crea una canción"),
                Map.of("metodo", "PUT", "ruta", "/api/canciones/{id}", "descripcion", "Actualiza una canción"),
                Map.of("metodo", "DELETE", "ruta", "/api/canciones/{id}", "descripcion", "Elimina una canción"),
                Map.of("metodo", "GET", "ruta", "/api/canciones/{id}/deezer", "descripcion", "Busca la canción en Deezer"),
                Map.of("metodo", "GET", "ruta", "/api/artistas", "descripcion", "Lista los artistas"),
                Map.of("metodo", "GET", "ruta", "/api/artistas/{id}/canciones", "descripcion", "Canciones de un artista")
        );
        return ResponseEntity.ok(Map.of(
                "nombre", "MusicaAPI — catálogo musical (Unidad 2)",
                "endpoints", endpoints
        ));
    }
}
