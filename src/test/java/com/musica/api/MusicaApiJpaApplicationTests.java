package com.musica.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Prueba de humo: verifica que el contexto de Spring arranca sin errores
 * (escaneo de componentes, datasource de H2, repositorio JPA, etc.).
 */
@SpringBootTest
class MusicaApiJpaApplicationTests {

    @Test
    void contextLoads() {
        // Si el contexto carga, la prueba pasa.
    }
}
