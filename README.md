# MusicaAPI JPA 🎵

API REST de un **catálogo musical** con persistencia de datos, desarrollada con **Java**, **Spring Boot**, **Spring Data JPA** e **Hibernate**.

Es la continuación de la Actividad Colaborativa #1 (`musica-api`), que guardaba las canciones en memoria. En esta versión, la misma API persiste las canciones en una base de datos (H2) mediante una entidad JPA y un repositorio `JpaRepository`, con operaciones **CRUD** completas (crear, consultar, actualizar y eliminar) y una **consulta personalizada**.

## Contexto seleccionado

**Música.** La API gestiona canciones de un catálogo: cada canción tiene un título, un artista, un género, un año y una duración en segundos. Permite listar el catálogo, consultar una canción por su identificador, filtrar por género (consulta personalizada), ver los géneros disponibles, y crear, actualizar y eliminar canciones.

## Integrantes

- Azel (individual)

## Tecnologías utilizadas

- **Java 22**
- **Spring Boot 3.4** (Spring Web, Tomcat embebido)
- **Spring Data JPA** (repositorios y `JpaRepository`)
- **Hibernate** (proveedor JPA: mapea la entidad a la tabla y genera el SQL)
- **H2** (base de datos embebida, en archivo)
- **Maven** (gestión de dependencias y build)
- **Jackson** (serialización/deserialización JSON)

## Estructura del proyecto

```
musica-api-jpa/
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java
    │   │   └── com/musica/api
    │   │       ├── MusicaApiJpaApplication.java   # punto de entrada
    │   │       ├── config
    │   │       │   └── DataInitializer.java       # datos de ejemplo (1ª ejecución)
    │   │       ├── controlador
    │   │       │   └── CancionController.java     # endpoints HTTP (ResponseEntity)
    │   │       ├── dto
    │   │       │   └── CancionDTO.java            # datos de entrada (record)
    │   │       ├── modelo
    │   │       │   └── Cancion.java               # entidad JPA (@Entity)
    │   │       ├── repositorio
    │   │       │   └── CancionRepository.java     # JpaRepository + consultas JPQL
    │   │       └── servicio
    │   │           └── CatalogoMusical.java       # lógica de negocio
    │   └── resources
    │       └── application.properties             # datasource H2 + JPA
    └── test
        └── java/com/musica/api
            ├── MusicaApiJpaApplicationTests.java  # prueba de contexto
            └── repositorio
                └── CancionRepositoryTest.java     # pruebas del repositorio
```

## Cómo ejecutar

Requisitos: JDK 17 o superior y Maven instalados.

```bash
mvn spring-boot:run
```

La aplicación queda escuchando en `http://localhost:8080`. La base de datos se guarda en el archivo `./data/cancionesdb.mv.db` (se crea automáticamente en la primera ejecución).

Para compilar y ejecutar las pruebas:

```bash
mvn test
```

### Consola de H2 (opcional)

Para inspeccionar la base de datos directamente, abre `http://localhost:8080/h2-console` y usa:

- **JDBC URL:** `jdbc:h2:file:./data/cancionesdb`
- **User:** `sa`
- **Password:** *(vacío)*

## Endpoints

| Método | URL                          | Descripción                                          | Respuestas |
|--------|------------------------------|------------------------------------------------------|------------|
| GET    | `/api/canciones`             | Lista todas las canciones                            | 200 OK |
| GET    | `/api/canciones/{id}`        | Canción por id (PathVariable)                        | 200 OK / 404 NOT FOUND |
| GET    | `/api/canciones/generos`     | Géneros disponibles sin repetir                      | 200 OK |
| GET    | `/api/canciones/buscar?genero=rock` | Consulta personalizada por género (RequestParam) | 200 OK / 400 BAD REQUEST |
| POST   | `/api/canciones`             | Crea una canción (JSON en el cuerpo)                 | 201 CREATED / 400 BAD REQUEST |
| PUT    | `/api/canciones/{id}`        | Actualiza una canción existente                      | 200 OK / 400 BAD REQUEST / 404 NOT FOUND |
| DELETE | `/api/canciones/{id}`        | Elimina una canción por id                           | 204 NO CONTENT / 404 NOT FOUND |

## Ejemplos de solicitud y respuesta

Crear una canción:

```bash
curl -X POST http://localhost:8080/api/canciones \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Mi Canción","artista":"Yo","genero":"Pop","anio":2026,"duracionSegundos":200}'
```

Respuesta (`201 Created`):

```json
{
  "id": 9,
  "titulo": "Mi Canción",
  "artista": "Yo",
  "genero": "Pop",
  "anio": 2026,
  "duracionSegundos": 200
}
```

Consultar la canción creada (`200 OK`):

```bash
curl http://localhost:8080/api/canciones/9
```

Consulta personalizada por género (`200 OK`):

```bash
curl "http://localhost:8080/api/canciones/buscar?genero=Rock%20en%20español"
```

Actualizar (`200 OK`) y eliminar (`204 No Content`):

```bash
curl -X PUT http://localhost:8080/api/canciones/9 \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Mi Canción (editada)","artista":"Yo","genero":"Pop","anio":2026,"duracionSegundos":210}'

curl -X DELETE http://localhost:8080/api/canciones/9
```

## Decisiones de diseño

- **Entidad `Cancion` con JPA**: la clase está anotada con `@Entity` y `@Table`, con `@Id` + `@GeneratedValue(IDENTITY)` para que el id lo genere la base de datos. Hibernate crea y actualiza la tabla automáticamente (`ddl-auto=update`).
- **Repositorio `CancionRepository`**: extiende `JpaRepository<Cancion, Long>`, por lo que el CRUD básico (`save`, `findAll`, `findById`, `deleteById`, ...) viene implementado por Spring Data sin escribir SQL.
- **Consulta personalizada con JPQL**: `buscarPorGenero` filtra por género sin distinguir mayúsculas/minúsculas y con coincidencia parcial (`LIKE`); `listarGeneros` devuelve los géneros distintos y ordenados.
- **Servicio separado del controlador**: el controlador solo traduce HTTP a llamadas al servicio; la lógica de negocio y la validación viven en `CatalogoMusical`.
- **DTO de entrada con `record`**: el cliente nunca envía el `id`; ese lo asigna la base de datos al persistir.
- **`ResponseEntity` en todos los métodos**: permite elegir el código HTTP correcto para cada situación (200, 201, 204, 400, 404).
- **H2 en archivo (no en memoria)**: los datos sobreviven al reinicio de la aplicación, lo que evidencia que la persistencia es real y no un simple arreglo en RAM.

## Uso de inteligencia artificial

Se utilizó inteligencia artificial generativa (Hermes / modelos de lenguaje) como apoyo para estructurar el proyecto, revisar la configuración de JPA/Hibernate y redactar parte de la documentación. El código final fue revisado, adaptado y verificado por el estudiante ejecutando la aplicación y probando cada endpoint con `curl`.
