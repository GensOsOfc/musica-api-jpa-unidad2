package com.musica.api.repositorio;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.musica.api.modelo.Cancion;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba de integración del repositorio.
 *
 * {@code @DataJpaTest} levanta una base H2 EN MEMORIA (aislada, distinta
 * de la del archivo) y verifica el mapeo de la entidad y las consultas.
 */
@DataJpaTest
class CancionRepositoryTest {

    @Autowired
    private CancionRepository repositorio;

    @Test
    void guardarYRecuperarUnaCancion() {
        Cancion guardada = repositorio.save(
                new Cancion("La Camisa Negra", "Juanes", "Pop rock", 2004, 214));

        assertThat(guardada.getId()).isNotNull();
        assertThat(repositorio.findById(guardada.getId())).isPresent();
        assertThat(repositorio.count()).isEqualTo(1);
    }

    @Test
    void buscarPorGeneroIgnoraMayusculasYCoincidenciasParciales() {
        repositorio.save(new Cancion("Oye Mi Amor", "Maná", "Rock en español", 1992, 273));
        repositorio.save(new Cancion("Matador", "Los Fabulosos Cadillacs", "Rock en español", 1993, 312));
        repositorio.save(new Cancion("La Rebelión", "Joe Arroyo", "Salsa", 1986, 287));

        List<Cancion> resultado = repositorio.buscarPorGenero("ROCK");

        assertThat(resultado).hasSize(2);
    }

    @Test
    void listarGenerosSinRepetidosYOrdenados() {
        repositorio.save(new Cancion("A", "X", "Salsa", 2000, 100));
        repositorio.save(new Cancion("B", "X", "Pop rock", 2000, 100));
        repositorio.save(new Cancion("C", "X", "Salsa", 2000, 100));

        List<String> generos = repositorio.listarGeneros();

        assertThat(generos).containsExactly("Pop rock", "Salsa");
    }
}
