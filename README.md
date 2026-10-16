# Monitoreo competitivo de precios - Plaza Vea (caso académico)

Prototipo de escritorio Java para monitoreo competitivo de precios.

## Tecnologías
- Java 21
- JavaFX
- Maven
- PostgreSQL
- JDBC
- Jsoup

## Requisitos
- JDK 21
- Maven 3.9+
- PostgreSQL 14+ recomendado

## 1. Crear base de datos
En PostgreSQL:

```sql
CREATE DATABASE monitoreo_precios;
```

Conéctate a `monitoreo_precios` y ejecuta:

```text
database/schema_monitoreo_precios_final.sql
database/datos_prueba.sql
```

## 2. Configurar la conexión

Copia:

```text
src/main/resources/application.properties.example
```

como:

```text
src/main/resources/application.properties
```

y cambia:

```properties
db.url=jdbc:postgresql://localhost:5432/monitoreo_precios
db.user=postgres
db.password=TU_PASSWORD
```

## 3. Ejecutar

Desde la carpeta del proyecto:

```powershell
mvn clean javafx:run
```

La ventana permite:
- probar las fórmulas de comparación;
- verificar la conexión PostgreSQL;
- ejecutar una prueba Jsoup asíncrona.

## Resultado esperado de la comparación de prueba

Con costo `80`, precio propio `120` y externo `105`:

- utilidad actual: 40.00
- margen actual: 33.33 %
- utilidad simulada: 25.00
- margen simulado: 23.81 %
- genera alerta: sí

## GitHub

```powershell
git init
git branch -M main
git add .
git commit -m "chore: estructura inicial del proyecto"
git remote add origin https://github.com/TU_USUARIO/monitoreo-precios-plaza-vea.git
git push -u origin main
```

> No subas `application.properties` con contraseñas reales.
