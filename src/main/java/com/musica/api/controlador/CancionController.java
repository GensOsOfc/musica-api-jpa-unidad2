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

import com.musica.api.dto.CancionDTO;
import com.musica.api.modelo.Cancion;
import com.musica.api.servicio.CatalogoMusical;

/**
 * Controlador REST del recurso "canciones".
 *
 * Todas las rutas parten de /api/canciones. Cada método devuelve un
 * {@link ResponseEntity} para controlar de forma explícita el código de
 * estado HTTP: 200 para lecturas, 201 al crear, 204 al eliminar, 400 para
 * peticiones inválidas y 404 cuando el recurso no existe.
 */
@RestController
@RequestMapping("/api/canciones")
public class CancionController {

    private final CatalogoMusical catalogo;

    public CancionController(CatalogoMusical catalogo) {
        this.catalogo = catalogo;
    }

    /**
     * GET /api/canciones → 200 OK con todas las canciones.
     */
    @GetMapping
    public ResponseEntity<List<Cancion>> listar() {
        return ResponseEntity.ok(catalogo.listarTodas());
    }

    /**
     * GET /api/canciones/generos → 200 OK con los géneros disponibles.
     */
    @GetMapping("/generos")
    public ResponseEntity<List<String>> listarGeneros() {
        return ResponseEntity.ok(catalogo.listarGeneros());
    }

    /**
     * GET /api/canciones/buscar?genero=rock
     * Consulta personalizada: filtra por género (RequestParam).
     * → 200 OK con el resultado, o 400 BAD REQUEST si falta el parámetro.
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorGenero(@RequestParam(required = false) String genero) {
        if (genero == null || genero.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El parámetro 'genero' es obligatorio. Ejemplo: /api/canciones/buscar?genero=rock"));
        }
        return ResponseEntity.ok(catalogo.buscarPorGenero(genero));
    }

    /**
     * GET /api/canciones/{id}
     * Busca una canción por su id (PathVariable).
     * → 200 OK si existe, 404 NOT FOUND si no.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Cancion> cancion = catalogo.buscarPorId(id);
        if (cancion.isPresent()) {
            return ResponseEntity.ok(cancion.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No existe una canción con id " + id));
    }

    /**
     * POST /api/canciones
     * Crea una canción (JSON en el cuerpo, deserializado a CancionDTO).
     * → 201 CREATED con la canción persistida y cabecera Location,
     *   o 400 BAD REQUEST si el JSON es inválido.
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CancionDTO dto) {
        String error = catalogo.validar(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }
        Cancion creada = catalogo.crear(dto);
        URI location = URI.create("/api/canciones/" + creada.getId());
        return ResponseEntity.created(location).body(creada);
    }

    /**
     * PUT /api/canciones/{id}
     * Actualiza una canción existente.
     * → 200 OK con la canción actualizada,
     *   o 400 BAD REQUEST si el JSON es inválido,
     *   o 404 NOT FOUND si no existe una canción con ese id.
     */
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

    /**
     * DELETE /api/canciones/{id}
     * Elimina una canción por su id.
     * → 204 NO CONTENT sin cuerpo si se eliminó,
     *   o 404 NOT FOUND si no existía.
     */
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
