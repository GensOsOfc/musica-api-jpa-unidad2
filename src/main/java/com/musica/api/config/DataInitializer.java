package com.musica.api.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.musica.api.modelo.Cancion;
import com.musica.api.repositorio.CancionRepository;

/**
 * Carga las canciones de ejemplo solo si la tabla está vacía, así no se
 * duplican al reiniciar (la base persiste en archivo).
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final CancionRepository repositorio;

    public DataInitializer(CancionRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void run(String... args) {
        if (repositorio.count() > 0) {
            return;
        }
        repositorio.save(new Cancion("La Camisa Negra", "Juanes", "Pop rock", 2004, 214));
        repositorio.save(new Cancion("Oye Mi Amor", "Maná", "Rock en español", 1992, 273));
        repositorio.save(new Cancion("La Rebelión", "Joe Arroyo", "Salsa", 1986, 287));
        repositorio.save(new Cancion("Matador", "Los Fabulosos Cadillacs", "Rock en español", 1993, 312));
        repositorio.save(new Cancion("De Música Ligera", "Soda Stereo", "Rock en español", 1990, 226));
        repositorio.save(new Cancion("Te Aviso, Te Anuncio", "Shakira", "Pop rock", 2001, 230));
        repositorio.save(new Cancion("La Tierra del Olvido", "Carlos Vives", "Vallenato", 1995, 251));
        repositorio.save(new Cancion("Afuera del Planeta", "Aterciopelados", "Rock alternativo", 2003, 238));
    }
}
