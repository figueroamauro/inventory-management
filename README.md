# Gestión de Inventarios

Aplicación web para registrar, actualizar y controlar el stock de productos en tiempo real, desarrollada con arquitectura de microservicios.

## Características

- Registro de productos y categorías.
- Actualización de stock en tiempo real.
- Búsqueda y filtrado de productos.
- Historial de movimientos de inventario.
- Control de niveles mínimos de stock.
- Arquitectura basada en microservicios para escalabilidad y mantenibilidad.

## Tecnologías utilizadas

- **Backend:** Java17 + Spring Boot 3.4
- **Frontend:** (...)
- **Base de datos:** MySQL
- **Testing:** Junit5 + Mockito + Testcontainers + DatabaseRider
- **Migraciones:** Flyway
- **Contenedores:** Docker
- **Comunicación entre microservicios:** REST APIs (Feign)
- **Documentación de APIs:** Swagger / OpenAPI

## Estructura del proyecto

- **User-Service:** Autenticación y autorización de usuarios.
- **Product-Service:** Gestión de productos, categorias y almacenes.
- **Stock-Service:** Registro de movimientos de stock.
