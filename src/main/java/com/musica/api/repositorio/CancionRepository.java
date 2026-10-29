package com.musica.api.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.musica.api.modelo.Cancion;

/**
 * Repositorio de la entidad {@link Cancion}.
 *
 * Al extender {@code JpaRepository<Cancion, Long>}, Spring Data genera
 * automáticamente la implementación con todas las operaciones CRUD:
 * save, findAll, findById, existsById, deleteById, count, etc. No hace
 * falta escribir ningún SQL para las operaciones básicas.
 *
 * Aquí se agregan consultas personalizadas (JPQL) que van más allá del CRUD.
 */
public interface CancionRepository extends JpaRepository<Cancion, Long> {

    /**
     * Consulta personalizada (JPQL): busca canciones cuyo género contenga
     * el texto recibido, sin distinguir mayúsculas/minúsculas, ordenadas
     * alfabéticamente por título.
     *
     * {@code :genero} es un parámetro nombrado que se enlaza con
     * {@code @Param("genero")}.
     */
    @Query("""
            SELECT c FROM Cancion c
            WHERE LOWER(c.genero) LIKE LOWER(CONCAT('%', :genero, '%'))
            ORDER BY c.titulo
            """)
    List<Cancion> buscarPorGenero(@Param("genero") String genero);

    /**
     * Consulta personalizada (JPQL): lista los géneros disponibles,
     * sin repetidos y en orden alfabético.
     */
    @Query("SELECT DISTINCT c.genero FROM Cancion c ORDER BY c.genero")
    List<String> listarGeneros();
}
