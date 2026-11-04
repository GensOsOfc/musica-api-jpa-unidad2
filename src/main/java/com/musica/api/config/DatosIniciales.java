package com.musica.api.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.musica.api.dto.CancionDTO;
import com.musica.api.repositorio.CancionRepository;
import com.musica.api.servicio.CatalogoMusical;

@Component
public class DatosIniciales implements CommandLineRunner {

    private final CancionRepository cancionRepository;
    private final CatalogoMusical catalogo;

    public DatosIniciales(CancionRepository cancionRepository, CatalogoMusical catalogo) {
        this.cancionRepository = cancionRepository;
        this.catalogo = catalogo;
    }

    @Override
    public void run(String... args) {
        if (cancionRepository.count() > 0) {
            return;
        }
        catalogo.agregar(new CancionDTO("La Camisa Negra", "Juanes", "Pop rock", 2004, 214));
        catalogo.agregar(new CancionDTO("Oye Mi Amor", "Maná", "Rock en español", 1992, 273));
        catalogo.agregar(new CancionDTO("La Rebelión", "Joe Arroyo", "Salsa", 1986, 287));
        catalogo.agregar(new CancionDTO("Matador", "Los Fabulosos Cadillacs", "Rock en español", 1993, 312));
        catalogo.agregar(new CancionDTO("De Música Ligera", "Soda Stereo", "Rock en español", 1990, 226));
        catalogo.agregar(new CancionDTO("Te Aviso, Te Anuncio", "Shakira", "Pop rock", 2001, 230));
        catalogo.agregar(new CancionDTO("La Tierra del Olvido", "Carlos Vives", "Vallenato", 1995, 251));
        catalogo.agregar(new CancionDTO("Afuera del Planeta", "Aterciopelados", "Rock alternativo", 2003, 238));
    }
}
