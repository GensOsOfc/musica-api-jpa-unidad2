package com.musica.api.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.musica.api.modelo.Cancion;

/**
 * JpaRepository ya trae el CRUD (save, findAll, findById, deleteById, ...).
 * Aquí solo se agregan las consultas personalizadas.
 */
public interface CancionRepository extends JpaRepository<Cancion, Long> {

    /** Canciones cuyo género contiene el texto, sin distinguir mayúsculas. */
    @Query("""
            SELECT c FROM Cancion c
            WHERE LOWER(c.genero) LIKE LOWER(CONCAT('%', :genero, '%'))
            ORDER BY c.titulo
            """)
    List<Cancion> buscarPorGenero(@Param("genero") String genero);

    /** Géneros distintos, en orden alfabético. */
    @Query("SELECT DISTINCT c.genero FROM Cancion c ORDER BY c.genero")
    List<String> listarGeneros();
}
