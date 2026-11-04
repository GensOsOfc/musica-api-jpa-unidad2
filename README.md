# MusicaAPI — catálogo musical con JPA, H2/MySQL y Deezer

API REST de un catálogo musical desarrollada con **Java**, **Spring Boot**, **Spring Data JPA** e **Hibernate**. Es la evolución de la Actividad Colaborativa #1: ahora las canciones y sus artistas se guardan en una base de datos (H2 o MySQL), y hay un endpoint que consulta la API pública de **Deezer** usando `RestClient`.

## Contexto seleccionado

**Música.** La API gestiona canciones (título, artista, género, año, duración) y artistas (nombre único). Permite las operaciones CRUD de canciones, consultas por género y por artista, y la búsqueda de una canción concreta en Deezer.

## Tecnologías utilizadas

- **Java 22**
- **Spring Boot 3.4** (Spring Web, Tomcat embebido)
- **Spring Data JPA + Hibernate** (mapeo objeto-relacional)
- **H2** (base embebida en memoria, perfil por defecto)
- **MySQL** (perfil alternativo, base relacional)
- **RestClient** (consumo de la API externa de Deezer)
- **Maven** y **Jackson** (JSON)

## Estructura del proyecto

```
musica-api-jpa/
├── pom.xml
├── README.md
├── bruno/                                  # colección de peticiones
└── src/main/
    ├── resources/
    │   ├── application.properties          # perfil activo (h2)
    │   ├── application-h2.properties       # H2 en memoria + consola
    │   └── application-mysql.properties    # MySQL local
    └── java/com/musica/api/
        ├── MusicaApiJpaApplication.java
        ├── config/DatosIniciales.java      # carga 8 canciones si la tabla está vacía
        ├── modelo/
        │   ├── Artista.java                 # @Entity
        │   └── Cancion.java                 # @Entity con @ManyToOne a Artista
        ├── dto/
        │   ├── CancionDTO.java              # record de entrada (igual que en la U1)
        │   └── CancionDeezerDTO.java        # record de salida del endpoint de Deezer
        ├── repositorio/
        │   ├── ArtistaRepository.java
        │   └── CancionRepository.java
        ├── externo/
        │   ├── DeezerCliente.java           # RestClient
        │   └── DeezerRespuesta.java         # records para el JSON de Deezer
        ├── excepcion/
        │   ├── ServicioExternoException.java
        │   └── ManejadorErrores.java        # @RestControllerAdvice (502)
        ├── servicio/CatalogoMusical.java
        └── controlador/
            ├── InicioController.java
            ├── CancionController.java
            └── ArtistaController.java
```

## Modelo de datos

- **Artista**: `id`, `nombre` (único y obligatorio).
- **Cancion**: `id`, `titulo`, `genero`, `anio`, `duracionSegundos` y `@ManyToOne` hacia **Artista** (muchas canciones, un artista).

El JSON **de entrada** del POST/PUT se mantiene igual que en la U1 (el artista se envía como texto y el servicio lo resuelve o lo crea). En las **respuestas**, el artista aparece como objeto anidado.

## Cómo ejecutar

Requisitos: JDK 17 o superior y Maven.

### Con H2 (por defecto)

```bash
mvn spring-boot:run
```

Consola de H2 en `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:musicadb`, usuario `sa`, sin contraseña).

### Con MySQL

Con MySQL escuchando en `localhost:3306`. Por defecto usa el usuario `root` sin contraseña; para cambiarlo, define las variables de entorno `DB_USER` y `DB_PASSWORD`:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

La base `musica_db` se crea automáticamente si no existe.

### Pruebas

```bash
mvn clean test
```

## Endpoints

| Método | Ruta | Descripción | Respuestas |
|--------|------|-------------|------------|
| GET | `/` | Índice con la lista de endpoints | 200 |
| GET | `/api/canciones` | Lista todas las canciones | 200 |
| GET | `/api/canciones/generos` | Géneros disponibles sin repetir | 200 |
| GET | `/api/canciones/{id}` | Canción por id | 200 / 404 |
| GET | `/api/canciones/buscar?genero=` | Filtra por género | 200 / 400 |
| POST | `/api/canciones` | Crea una canción | 201 + Location / 400 |
| PUT | `/api/canciones/{id}` | Actualiza una canción | 200 / 400 / 404 |
| DELETE | `/api/canciones/{id}` | Elimina una canción | 204 / 404 |
| GET | `/api/canciones/{id}/deezer` | Busca la canción en Deezer | 200 / 404 / 502 |
| GET | `/api/artistas` | Lista los artistas | 200 |
| GET | `/api/artistas/{id}/canciones` | Canciones de un artista | 200 / 404 |

## Ejemplos

Crear una canción (el mismo JSON de la U1):

```bash
curl -X POST http://localhost:8080/api/canciones \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Mi Canción","artista":"Yo","genero":"Pop","anio":2026,"duracionSegundos":200}'
```

Respuesta (`201 Created`), nótese el artista anidado:

```json
{
  "id": 9,
  "titulo": "Mi Canción",
  "genero": "Pop",
  "anio": 2026,
  "duracionSegundos": 200,
  "artista": { "id": 9, "nombre": "Yo" }
}
```

Buscar en Deezer:

```bash
curl http://localhost:8080/api/canciones/1/deezer
```

Respuesta (`200 OK`):

```json
{
  "titulo": "La Camisa Negra",
  "artista": "Juanes",
  "album": "Mi Sangre",
  "portada": "https://cdns-images.dzcdn.net/images/...",
  "preview": "https://cdns-preview-...",
  "link": "https://www.deezer.com/track/..."
}
```

## Probar con Bruno

El proyecto incluye una colección en `bruno/`:

1. Abrir Bruno → **Open Collection** → seleccionar la carpeta `bruno/`.
2. Con la app corriendo, ejecutar las peticiones en orden.
3. Las peticiones `9. Listar artistas` y `10. Buscar en Deezer` prueban los endpoints nuevos (la 10 usa la canción con id 1 = *La Camisa Negra*, que requiere conexión a internet).

## Manejo de errores

Si la API de Deezer falla o no responde, `ManejadorErrores` (`@RestControllerAdvice`) devuelve `502 Bad Gateway` con `{"error": "..."}`. Si la canción no existe en la base o Deezer no la encuentra, el endpoint responde `404` con un mensaje distinto para cada caso.

## Uso de inteligencia artificial

Se utilizó inteligencia artificial generativa (Hermes / modelos de lenguaje) como apoyo para estructurar el proyecto, revisar la configuración de JPA/Hibernate y de los perfiles H2/MySQL, y adaptar el consumo de Deezer con `RestClient`. El código final fue revisado, adaptado y verificado por el estudiante ejecutando `mvn clean test` y probando cada endpoint (incluido el de Deezer) con sus códigos HTTP reales.
