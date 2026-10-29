package com.musica.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada. @SpringBootApplication habilita el escaneo de componentes
 * y la configuración automática (web, JPA y datasource H2).
 */
@SpringBootApplication
public class MusicaApiJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicaApiJpaApplication.class, args);
    }
}
