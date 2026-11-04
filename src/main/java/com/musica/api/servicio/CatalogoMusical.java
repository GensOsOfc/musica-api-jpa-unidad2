package com.musica.api.servicio;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.musica.api.dto.CancionDTO;
import com.musica.api.externo.DeezerCliente;
import com.musica.api.externo.DeezerRespuesta;
import com.musica.api.modelo.Artista;
import com.musica.api.modelo.Cancion;
import com.musica.api.repositorio.ArtistaRepository;
import com.musica.api.repositorio.CancionRepository;

@Service
public class CatalogoMusical {

    private final CancionRepository cancionRepository;
    private final ArtistaRepository artistaRepository;
    private final DeezerCliente deezerCliente;

    public CatalogoMusical(CancionRepository cancionRepository,
                           ArtistaRepository artistaRepository,
                           DeezerCliente deezerCliente) {
        this.cancionRepository = cancionRepository;
        this.artistaRepository = artistaRepository;
        this.deezerCliente = deezerCliente;
    }

    public List<Cancion> listarTodas() {
        return cancionRepository.findAll();
    }

    public Optional<Cancion> buscarPorId(Long id) {
        return cancionRepository.findById(id);
    }

    public List<Cancion> buscarPorGenero(String genero) {
        return cancionRepository.findByGeneroContainingIgnoreCase(genero.trim());
    }

    public List<String> listarGeneros() {
        return cancionRepository.listarGeneros();
    }

    public boolean eliminar(Long id) {
        if (!cancionRepository.existsById(id)) {
            return false;
        }
        cancionRepository.deleteById(id);
        return true;
    }

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

    @Transactional
    public Cancion agregar(CancionDTO dto) {
        Artista artista = resolverArtista(dto.artista().trim());
        Cancion cancion = new Cancion(
                dto.titulo().trim(),
                artista,
                dto.genero().trim(),
                dto.anio(),
                dto.duracionSegundos()
        );
        return cancionRepository.save(cancion);
    }

    @Transactional
    public Optional<Cancion> actualizar(Long id, CancionDTO dto) {
        Optional<Cancion> existente = cancionRepository.findById(id);
        if (existente.isEmpty()) {
            return Optional.empty();
        }
        Cancion cancion = existente.get();
        cancion.setTitulo(dto.titulo().trim());
        cancion.setArtista(resolverArtista(dto.artista().trim()));
        cancion.setGenero(dto.genero().trim());
        cancion.setAnio(dto.anio());
        cancion.setDuracionSegundos(dto.duracionSegundos());
        return Optional.of(cancionRepository.save(cancion));
    }

    public Optional<DeezerRespuesta.DeezerTrack> buscarEnDeezer(Long id) {
        Optional<Cancion> cancion = cancionRepository.findById(id);
        if (cancion.isEmpty()) {
            return Optional.empty();
        }
        return deezerCliente.buscar(cancion.get().getTitulo(), cancion.get().getArtista().getNombre());
    }

    private Artista resolverArtista(String nombre) {
        return artistaRepository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> artistaRepository.save(new Artista(nombre)));
    }
}
