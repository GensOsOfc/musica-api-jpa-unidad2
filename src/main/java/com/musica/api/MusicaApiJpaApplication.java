package com.musica.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación.
 *
 * {@code @SpringBootApplication} habilita el escaneo de componentes,
 * la configuración automática (incluida la de Spring Data JPA y el
 * datasource de H2) y el arranque del Tomcat embebido.
 */
@SpringBootApplication
public class MusicaApiJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicaApiJpaApplication.class, args);
    }
}
