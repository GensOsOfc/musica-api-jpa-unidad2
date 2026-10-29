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
 * Lógica de negocio. Delega la persistencia en CancionRepository.
 */
@Service
public class CatalogoMusical {

    private final CancionRepository repositorio;

    public CatalogoMusical(CancionRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Cancion> listarTodas() {
        return repositorio.findAll(Sort.by("titulo"));
    }

    public Optional<Cancion> buscarPorId(Long id) {
        return repositorio.findById(id);
    }

    public List<Cancion> buscarPorGenero(String genero) {
        return repositorio.buscarPorGenero(genero.trim());
    }

    public List<String> listarGeneros() {
        return repositorio.listarGeneros();
    }

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

    /** @return empty si no existe una canción con ese id. */
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
        // Entidad gestionada: Hibernate ejecuta el UPDATE al cerrar la transacción.
        return Optional.of(repositorio.save(cancion));
    }

    /** @return true si existía y fue eliminada, false si no. */
    public boolean eliminar(Long id) {
        if (!repositorio.existsById(id)) {
            return false;
        }
        repositorio.deleteById(id);
        return true;
    }

    /** @return mensaje de error o null si el DTO es válido. */
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
