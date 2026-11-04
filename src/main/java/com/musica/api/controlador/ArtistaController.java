package com.musica.api.controlador;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musica.api.modelo.Artista;
import com.musica.api.modelo.Cancion;
import com.musica.api.repositorio.ArtistaRepository;
import com.musica.api.repositorio.CancionRepository;

@RestController
@RequestMapping("/api/artistas")
public class ArtistaController {

    private final ArtistaRepository artistaRepository;
    private final CancionRepository cancionRepository;

    public ArtistaController(ArtistaRepository artistaRepository, CancionRepository cancionRepository) {
        this.artistaRepository = artistaRepository;
        this.cancionRepository = cancionRepository;
    }

    @GetMapping
    public ResponseEntity<List<Artista>> listar() {
        return ResponseEntity.ok(artistaRepository.findAll());
    }

    @GetMapping("/{id}/canciones")
    public ResponseEntity<?> cancionesDeArtista(@PathVariable Long id) {
        Optional<Artista> artista = artistaRepository.findById(id);
        if (artista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No existe un artista con id " + id));
        }
        List<Cancion> canciones = cancionRepository.findByArtistaNombreIgnoreCase(artista.get().getNombre());
        return ResponseEntity.ok(canciones);
    }
}
