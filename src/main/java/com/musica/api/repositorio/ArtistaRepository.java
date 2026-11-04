package com.musica.api.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.musica.api.modelo.Artista;

public interface ArtistaRepository extends JpaRepository<Artista, Long> {

    Optional<Artista> findByNombreIgnoreCase(String nombre);
}
