package com.musica.api.servicio;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.musica.api.dto.CancionDTO;
import com.musica.api.modelo.Cancion;
import com.musica.api.repositorio.CancionRepository;

/**
 * Lógica de negocio del catálogo musical.
 *
 * A diferencia de la Actividad #1 (que guardaba todo en una lista en
 * memoria), ahora el servicio delega la persistencia en un
 * {@link CancionRepository} de Spring Data JPA. El repositorio se encarga
 * de traducir cada operación a SQL y de guardar/leer las filas en H2.
 */
@Service
public class CatalogoMusical {

    private final CancionRepository repositorio;

    // Inyección por constructor: Spring entrega la instancia del repositorio.
    public CatalogoMusical(CancionRepository repositorio) {
        this.repositorio = repositorio;
    }

    /** Devuelve todas las canciones, ordenadas por título. */
    public List<Cancion> listarTodas() {
        return repositorio.findAll(Sort.by("titulo"));
    }

    /** Busca una canción por su id (clave primaria). */
    public Optional<Cancion> buscarPorId(Long id) {
        return repositorio.findById(id);
    }

    /** Filtra canciones por género usando la consulta personalizada del repositorio. */
    public List<Cancion> buscarPorGenero(String genero) {
        return repositorio.buscarPorGenero(genero.trim());
    }

    /** Lista los géneros disponibles, sin repetidos y en orden alfabético. */
    public List<String> listarGeneros() {
        return repositorio.listarGeneros();
    }

    /**
     * Crea y persiste una canción nueva. El id lo genera la base de datos.
     */
    public Cancion crear(CancionDTO dto) {
        Cancion cancion = new Cancion(
                dto.titulo().trim(),
                dto.artista().trim(),
                dto.genero().trim(),
                dto.anio(),
                dto.duracionSegundos()
        );
        return repositorio.save(cancion);
    }

    /**
     * Actualiza una canción existente.
     *
     * @return un {@code Optional} con la canción actualizada si existía,
     *         o {@code Optional.empty()} si no había canción con ese id.
     */
    public Optional<Cancion> actualizar(Long id, CancionDTO dto) {
        Optional<Cancion> existente = repositorio.findById(id);
        if (existente.isEmpty()) {
            return Optional.empty();
        }
        Cancion cancion = existente.get();
        cancion.setTitulo(dto.titulo().trim());
        cancion.setArtista(dto.artista().trim());
        cancion.setGenero(dto.genero().trim());
        cancion.setAnio(dto.anio());
        cancion.setDuracionSegundos(dto.duracionSegundos());
        // La entidad ya está "gestionada" por JPA: al terminar la transacción,
        // Hibernate detecta los cambios y ejecuta el UPDATE automáticamente.
        return Optional.of(repositorio.save(cancion));
    }

    /**
     * Elimina una canción por su id.
     *
     * @return {@code true} si la canción existía y fue eliminada,
     *         {@code false} si no había ninguna canción con ese id.
     */
    public boolean eliminar(Long id) {
        if (!repositorio.existsById(id)) {
            return false;
        }
        repositorio.deleteById(id);
        return true;
    }

    /**
     * Valida un DTO antes de crear o actualizar una canción.
     *
     * @return un mensaje de error si el DTO no es válido, o {@code null}
     *         si todo está bien.
     */
    public String validar(CancionDTO dto) {
        if (dto.titulo() == null || dto.titulo().isBlank()) {
            return "El campo 'titulo' es obligatorio";
        }
        if (dto.artista() == null || dto.artista().isBlank()) {
            return "El campo 'artista' es obligatorio";
        }
        if (dto.genero() == null || dto.genero().isBlank()) {
            return "El campo 'genero' es obligatorio";
        }
        if (dto.duracionSegundos() <= 0) {
            return "El campo 'duracionSegundos' debe ser mayor que 0";
        }
        if (dto.anio() < 1900 || dto.anio() > Year.now().getValue()) {
            return "El campo 'anio' debe estar entre 1900 y " + Year.now().getValue();
        }
        return null;
    }
}
