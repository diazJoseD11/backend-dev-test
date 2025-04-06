# Startrack Demo - Backend HTMX con Ktor y Docker 🧪🚀

Este proyecto es una demostración de un backend desarrollado con [Ktor](https://ktor.io/) en Kotlin, utilizando [HTMX](https://htmx.org/) para interacciones dinámicas en el frontend. Se conecta a una base de datos PostgreSQL y permite explorar elementos del universo de CS:GO como skins, agentes, cajas y llaves. Además, integra autenticación básica y exportación a XML.

---

## 🐳 Levantar el Proyecto con Docker

Asegúrate de tener instalado Docker y Docker Compose.

```bash
docker-compose up --build
```

Accede desde tu navegador a:

```
http://localhost:8080
```

---

## 🔐 Acceso protegido

La sección de Keys está protegida por autenticación básica.

| Usuario | Contraseña |
|---------|------------|
| admin   | 1234       |

Puedes iniciar sesión en: [http://localhost:8080/login](http://localhost:8080/login)

---

## 🌐 Rutas disponibles

| Ruta                     | Descripción                         |
|--------------------------|-------------------------------------|
| `/`                      | Página principal del demo           |
| `/skins`                 | Listado y buscador de skins         |
| `/agents`                | Listado y buscador de agentes       |
| `/crates`                | Listado y buscador de cajas (crates)|
| `/keys`                  | Protegido. Listado de llaves        |
| `/login`                 | Formulario de login                 |
| `/logout`                | Cierra la sesión                    |
| `/skins/export/xml`      | Exporta búsqueda de skins a XML     |

> Puedes usar el parámetro `query`, por ejemplo:  
> [http://localhost:8080/skins/export/xml?query=ak](http://localhost:8080/skins/export/xml?query=ak)

---

## 🧪 Estructura del Proyecto

```
src/
└── main/kotlin/stsa/kotlin_htmx/
    ├── models/        # DTOs y tablas con Exposed
    ├── services/      # Lógica de negocio por entidad
    ├── pages/         # Renderizado HTML con Ktor DSL
    ├── plugins/       # Configuración (DB, rutas, etc)
    ├── Application.kt # Punto de entrada principal
    └── Routes.kt      # Definición de rutas
```

---

## 🧼 Comandos útiles para desarrollo

Compilar localmente (si no usás Docker):

```bash
./gradlew build
```

Compilar jar ejecutable:

```bash
./gradlew shadowJar
```

---

## 📦 Variables de entorno

El proyecto usa un archivo `.env.default` para cargar configuraciones como:

```env
LOOKUP_API_KEY=1234567890
KTOR_DEVELOPMENT=true
```

Puedes renombrarlo a `.env.local` si deseas personalizarlo.

---

