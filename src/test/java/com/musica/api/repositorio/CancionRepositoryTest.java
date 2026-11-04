package com.musica.api.repositorio;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.musica.api.modelo.Artista;
import com.musica.api.modelo.Cancion;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CancionRepositoryTest {

    @Autowired
    private CancionRepository cancionRepository;

    @Autowired
    private ArtistaRepository artistaRepository;

    @Test
    void guardarYRecuperarUnaCancionConArtista() {
        Artista artista = artistaRepository.save(new Artista("Juanes"));
        Cancion guardada = cancionRepository.save(
                new Cancion("La Camisa Negra", artista, "Pop rock", 2004, 214));

        assertThat(guardada.getId()).isNotNull();
        assertThat(cancionRepository.findById(guardada.getId())).isPresent();
        assertThat(cancionRepository.findById(guardada.getId()).get().getArtista().getNombre())
                .isEqualTo("Juanes");
        assertThat(cancionRepository.count()).isEqualTo(1);
    }

    @Test
    void buscarPorGeneroIgnoraMayusculasYCoincidenciasParciales() {
        Artista man = artistaRepository.save(new Artista("Maná"));
        Artista mat = artistaRepository.save(new Artista("Los Fabulosos Cadillacs"));
        Artista joe = artistaRepository.save(new Artista("Joe Arroyo"));
        cancionRepository.save(new Cancion("Oye Mi Amor", man, "Rock en español", 1992, 273));
        cancionRepository.save(new Cancion("Matador", mat, "Rock en español", 1993, 312));
        cancionRepository.save(new Cancion("La Rebelión", joe, "Salsa", 1986, 287));

        List<Cancion> resultado = cancionRepository.findByGeneroContainingIgnoreCase("ROCK");

        assertThat(resultado).hasSize(2);
    }

    @Test
    void listarGenerosSinRepetidosYOrdenados() {
        Artista a = artistaRepository.save(new Artista("X"));
        cancionRepository.save(new Cancion("A", a, "Salsa", 2000, 100));
        cancionRepository.save(new Cancion("B", a, "Pop rock", 2000, 100));
        cancionRepository.save(new Cancion("C", a, "Salsa", 2000, 100));

        List<String> generos = cancionRepository.listarGeneros();

        assertThat(generos).containsExactly("Pop rock", "Salsa");
    }
}
