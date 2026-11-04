package com.musica.api.controlador;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.musica.api.dto.CancionDeezerDTO;
import com.musica.api.dto.CancionDTO;
import com.musica.api.externo.DeezerRespuesta;
import com.musica.api.modelo.Cancion;
import com.musica.api.servicio.CatalogoMusical;

@RestController
@RequestMapping("/api/canciones")
public class CancionController {

    private final CatalogoMusical catalogo;

    public CancionController(CatalogoMusical catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping
    public ResponseEntity<List<Cancion>> listar() {
        return ResponseEntity.ok(catalogo.listarTodas());
    }

    @GetMapping("/generos")
    public ResponseEntity<List<String>> listarGeneros() {
        return ResponseEntity.ok(catalogo.listarGeneros());
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorGenero(@RequestParam(required = false) String genero) {
        if (genero == null || genero.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El parámetro 'genero' es obligatorio. Ejemplo: /api/canciones/buscar?genero=rock"));
        }
        return ResponseEntity.ok(catalogo.buscarPorGenero(genero));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Cancion> cancion = catalogo.buscarPorId(id);
        if (cancion.isPresent()) {
            return ResponseEntity.ok(cancion.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No existe una canción con id " + id));
    }

    @GetMapping("/{id}/deezer")
    public ResponseEntity<?> buscarEnDeezer(@PathVariable Long id) {
        Optional<Cancion> cancion = catalogo.buscarPorId(id);
        if (cancion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No existe una canción con id " + id + " en la base de datos"));
        }
        Optional<DeezerRespuesta.DeezerTrack> track = catalogo.buscarEnDeezer(id);
        if (track.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Deezer no encontró la canción '" + cancion.get().getTitulo() + "'"));
        }
        DeezerRespuesta.DeezerTrack t = track.get();
        CancionDeezerDTO dto = new CancionDeezerDTO(
                cancion.get().getTitulo(),
                cancion.get().getArtista().getNombre(),
                t.album() != null ? t.album().title() : null,
                t.album() != null ? t.album().coverMedium() : null,
                t.preview(),
                t.link()
        );
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CancionDTO dto) {
        String error = catalogo.validar(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }
        Cancion creada = catalogo.agregar(dto);
        URI location = URI.create("/api/canciones/" + creada.getId());
        return ResponseEntity.created(location).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody CancionDTO dto) {
        String error = catalogo.validar(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }
        Optional<Cancion> actualizada = catalogo.actualizar(id, dto);
        if (actualizada.isPresent()) {
            return ResponseEntity.ok(actualizada.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No existe una canción con id " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        boolean eliminada = catalogo.eliminar(id);
        if (eliminada) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No existe una canción con id " + id));
    }
}
