# Room404 Backend

## Descripción del Proyecto

Room404 es una aplicación de rompecabezas (puzzles) interactiva desarrollada como proyecto backend en Spring Boot para el **Proyecto Intermodular** de la universidad. La aplicación permite a los usuarios registrarse, autenticarse, resolver puzzles de diferentes dificultades y hacer seguimiento de su progreso. Incluye funcionalidades como autenticación JWT, integración con Google OAuth, envío de correos electrónicos para recuperación de contraseña, y un sistema de rankings.

**Nota:** Este repositorio contiene únicamente el backend de la aplicación. El frontend correspondiente se desarrollará por separado y se conectará a este servidor una vez esté activo.

## Mecánicas de Juego

Room404 es un juego de puzzles con temática de ciberseguridad y "Matrix". Las mecánicas principales incluyen:

### Puzzles y Dificultades
- **Puzzles**: Rompecabezas con diferentes niveles de dificultad (1-5)
- **Solución Hash**: Las soluciones se almacenan hasheadas por seguridad
- **Puntuación Máxima**: Cada puzzle tiene una puntuación máxima posible

### Progreso y Rankings
- **Seguimiento de Estado**: NOT_STARTED, IN_PROGRESS, COMPLETED
- **Tiempo de Resolución**: Registro del tiempo empleado en segundos
- **Rankings Globales**: Clasificación de usuarios por puntuación total y tiempo

### Sistema de Correos
- **Correos Falsos**: Emails simulados asociados a puzzles específicos
- **Email de Malware**: Funcionalidad especial que envía un email temático con pistas del juego
- **Recuperación de Contraseña**: Sistema completo de reset vía email

### Datos Estáticos
- **Archivos JSON**: Datos estáticos utilizados en los puzzles del juego

### Autenticación Temática
- **JWT con Tema Matrix**: Tokens con expiración de 10 días
- **Google OAuth**: Integración con Google para login alternativo
- **Roles**: Sistema de usuarios normales y administradores

## Arquitectura del Proyecto

La aplicación sigue una arquitectura RESTful con separación clara de responsabilidades:

```
src/main/java/Cocky_Camel/Room404/
├── Room404Application.java          # Clase principal de Spring Boot
├── Controllers/                      # Controladores REST
│   ├── UserController.java          # Gestión de usuarios y autenticación
│   ├── PuzzleController.java        # Operaciones con puzzles
│   ├── UserProgressController.java  # Seguimiento de progreso
│   └── FakeEmailController.java     # Gestión de correos falsos
├── Services/                         # Lógica de negocio
│   ├── EmailService.java            # Servicio de envío de correos
│   └── PasswordResetService.java    # Servicio de recuperación de contraseña
├── Repositories/                     # Capa de acceso a datos
│   ├── UserRepository.java
│   ├── PuzzleRepository.java
│   ├── UserProgressRepository.java
│   └── FakeEmailRepository.java
├── Models/Entities/                  # Entidades JPA
│   ├── User.java                    # Usuario con roles y premium
│   ├── Puzzle.java                  # Puzzle con dificultad y solución
│   ├── UserProgress.java            # Progreso del usuario en puzzles
│   ├── FakeEmail.java               # Correos simulados
│   └── RankingDTO.java              # DTO para rankings
├── Security/                         # Configuración de seguridad
│   ├── SecurityConfig.java          # Configuración Spring Security
│   └── JwtUtil.java                 # Utilidades JWT
└── Resources/
    ├── application.properties       # Configuración de la aplicación
```

### Patrón Arquitectónico

- **MVC (Model-View-Controller)**: Separación entre datos, lógica y presentación
- **Repository Pattern**: Abstracción del acceso a datos
- **Service Layer**: Lógica de negocio centralizada
- **DTO Pattern**: Transferencia de datos optimizada

## Tecnologías Utilizadas

### Backend
- **Java 17**: Lenguaje de programación principal
- **Spring Boot 3.5.6**: Framework para desarrollo de aplicaciones
- **Spring Web**: Para APIs REST
- **Spring Data JPA**: Para persistencia de datos
- **Spring Security**: Para autenticación y autorización
- **Spring Mail**: Para envío de correos electrónicos

### Base de Datos
- **MySQL**: Base de datos relacional
- **Hibernate**: ORM para mapeo objeto-relacional

### Autenticación y Seguridad
- **JWT (JSON Web Tokens)**: Para autenticación stateless
- **BCrypt**: Para hash de contraseñas
- **Google OAuth 2.0**: Para login con Google

### Comunicación
- **SMTP**: Para envío de correos (Gmail)
- **Google API Client**: Para verificación de tokens OAuth

### Desarrollo y Testing
- **Maven**: Gestión de dependencias y build
- **JUnit 5**: Framework de testing
- **Spring Boot Test**: Testing integrado

## Prerrequisitos

Antes de ejecutar la aplicación, asegúrate de tener instalados:

- **Java 17** o superior
- **Maven 3.6+** para gestión de dependencias
- **MySQL 8.0+** para la base de datos
- **Git** para control de versiones

## Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone https://github.com/EricRodriguezRojo/Cocky_Camel_Room404_BE.git
cd Cocky_Camel_Room404_BE
```

### 2. Configurar la Base de Datos

La aplicación está configurada para usar MySQL. Las credenciales están en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://room404_mayfairmud:e508df5cfd4122a5c75cfd26e69fcbca9eef00a7@5ui413.h.filess.io:3306/room404_mayfairmud
spring.datasource.username=room404_mayfairmud
spring.datasource.password=e508df5cfd4122a5c75cfd26e69fcbca9eef00a7
```

**Nota:** Para desarrollo local, puedes cambiar estas configuraciones para apuntar a una base de datos MySQL local.

### 3. Configurar Correo Electrónico

Para funcionalidades de recuperación de contraseña, configura las credenciales SMTP en `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-app-password
```

### 4. Instalar Dependencias

```bash
mvn clean install
```

## Ejecución de la Aplicación

### Modo Desarrollo

```bash
mvn spring-boot:run
```

La aplicación se iniciará en `http://localhost:8080` por defecto.

### Construir y Ejecutar JAR

```bash
mvn clean package
java -jar target/hospital-0.0.1-SNAPSHOT.jar
```

### Verificar que Funciona

Una vez iniciada, puedes probar el endpoint de prueba:

```bash
curl http://localhost:8080/user/prueba/test
```

Respuesta esperada: `Hello test, ¡bienvenido a Room404!`

## API Endpoints

### Autenticación y Usuarios

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/user/login/{email}/{password}` | Login con email y contraseña |
| POST | `/api/user/google-login` | Login con Google OAuth |
| POST | `/api/user/forgot-password` | Solicitar reset de contraseña |
| POST | `/api/user/verify-reset-token` | Verificar token de reset |
| POST | `/api/user/reset-password` | Resetear contraseña |
| POST | `/api/user` | Registrar nuevo usuario |
| GET | `/api/users` | Obtener todos los usuarios (admin) |
| GET | `/api/user/email/{email}` | Obtener usuario por email |
| GET | `/api/user/{id}` | Obtener usuario por ID |
| PUT | `/api/user/{id}` | Actualizar usuario |
| DELETE | `/api/user/{id}` | Eliminar usuario |

### Puzzles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/puzzles` | Obtener todos los puzzles |
| GET | `/api/puzzles?difficulty={level}` | Obtener puzzles por dificultad |
| GET | `/api/puzzle/{id}` | Obtener puzzle específico |
| POST | `/api/puzzle` | Crear nuevo puzzle (admin) |
| PUT | `/api/puzzle/{id}` | Actualizar puzzle (admin) |
| DELETE | `/api/puzzle/{id}` | Eliminar puzzle (admin) |

### Progreso de Usuario

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/progress?userId={id}` | Obtener progreso de usuario |
| GET | `/api/progress?userId={id}&puzzleId={pid}` | Obtener progreso específico |
| POST | `/api/progress` | Crear/actualizar progreso |
| POST | `/api/progress/complete/{puzzleName}` | Completar puzzle con tiempo |
| PUT | `/api/progress/{id}` | Actualizar progreso |
| DELETE | `/api/progress/{id}` | Eliminar progreso |

### Rankings

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/ranking` | Obtener rankings generales |

### Correos Falsos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/emails` | Obtener correos falsos |
| GET | `/api/emails/puzzle/{puzzleId}` | Obtener correos por puzzle |
| POST | `/api/emails` | Crear correo falso |
| PUT | `/api/emails/{id}` | Actualizar correo falso |
| DELETE | `/api/emails/{id}` | Eliminar correo falso |

### Juego

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/game/trigger-malware` | Enviar email de malware (requiere auth) |

## Esquema de Base de Datos

### Tabla `users`
```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    nickname VARCHAR(100),
    password VARCHAR(255),
    google_uid VARCHAR(255),
    role ENUM('User', 'Admin') DEFAULT 'User',
    is_premium BOOLEAN DEFAULT FALSE
);
```

### Tabla `puzzles`
```sql
CREATE TABLE puzzles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    difficulty INT NOT NULL,
    solution_hash VARCHAR(255) NOT NULL,
    max_score INT NOT NULL
);
```

### Tabla `fake_emails`
```sql
CREATE TABLE fake_emails (
    id INT PRIMARY KEY AUTO_INCREMENT,
    sender VARCHAR(255),
    body_text TEXT,
    puzzle_id INT,
    FOREIGN KEY (puzzle_id) REFERENCES puzzles(id)
);
```

### Tabla `fake_emails`
```sql
CREATE TABLE fake_emails (
    id INT PRIMARY KEY AUTO_INCREMENT,
    sender VARCHAR(255),
    recipient VARCHAR(255),
    subject VARCHAR(255),
    body TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Autenticación

La aplicación utiliza JWT para autenticación stateless:

1. **Registro/Login**: Los usuarios pueden registrarse o hacer login con email/contraseña o Google OAuth
2. **Token JWT**: Al autenticarse, se devuelve un token JWT que debe incluirse en el header `Authorization: Bearer <token>`
3. **Verificación**: Los endpoints protegidos verifican la validez del token
4. **Roles**: Sistema de roles (User/Admin) para control de acceso
5. **Seguridad**: Actualmente configurada en modo desarrollo (sin restricciones CSRF), debe ajustarse para producción

### JWT Configuration
- **Secret Key**: Fijo en código (debe externalizarse en producción)
- **Expiración**: 10 días (864000000 ms)
- **Algoritmo**: HMAC-SHA

### Ejemplo de Uso de API con Autenticación

```bash
# 1. Login (nota: credenciales en URL - mejorar seguridad en producción)
curl -X POST http://localhost:8080/api/user/login/user@example.com/password123

# Respuesta esperada:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login correcto. Bienvenido/a User",
  "role": "User"
}

# 2. Usar token en requests
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  http://localhost:8080/api/puzzles
```

## Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

### Guías de Código

- Sigue las convenciones de Java
- Usa nombres descriptivos para variables y métodos
- Incluye documentación JavaDoc
- Escribe tests para nuevas funcionalidades

## Contacto

**Autores del Proyecto:**
- **Eric Rodriguez Rojo** - pl2024431@365.stucom.com
- **Marc Fernández González** - pl2025196@365.stucom.com  
- **Abril Palau Pardillos** - pl2024243@365.stucom.com

**Repositorio GitHub:** [https://github.com/EricRodriguezRojo/Cocky_Camel_Room404_BE](https://github.com/EricRodriguezRojo/Cocky_Camel_Room404_BE)

---

<parameter name="filePath">c:\Users\lemvo\git\Cocky_Camel_Room404_BE\README.md