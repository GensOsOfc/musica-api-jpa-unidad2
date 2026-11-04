package com.musica.api.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.musica.api.modelo.Cancion;

public interface CancionRepository extends JpaRepository<Cancion, Long> {

    List<Cancion> findByGeneroContainingIgnoreCase(String genero);

    List<Cancion> findByArtistaNombreIgnoreCase(String nombre);

    @Query("select distinct c.genero from Cancion c order by c.genero")
    List<String> listarGeneros();
}
