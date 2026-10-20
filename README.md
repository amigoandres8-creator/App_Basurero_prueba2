# App_Basurero

App Android para controlar y monitorear un **basurero automático** (ESP32 + sensor ultrasónico + servo).
Proyecto individual del ramo **Aplicaciones Móviles para IoT (TI3042)**, INACAP Rancagua.

- **Lenguaje:** Kotlin
- **IDE:** Android Studio
- **Base de datos:** Firebase Realtime Database (en la nube)
- **Conexión:** WiFi / Internet (HTTPS con TLS, lo maneja Firebase)

## Pantallas

| Pantalla | Qué hace |
|---|---|
| Login | Inicia sesión con Rut y contraseña contra Firebase |
| Crear Cuenta | Registra un usuario nuevo en la colección `usuarios` |
| Menú | Muestra el usuario logueado y lleva a las demás pantallas |
| Manipular Circuito | Activa o desactiva el circuito. El estado se ve **en tiempo real** en todos los celulares |
| Ver Acciones | Lista las acciones guardadas en Firebase (la más nueva arriba) |
| Estadísticas | Cuenta activaciones, desactivaciones, obstáculos y distancia total |
| Ver Perfil | Muestra los datos del usuario logueado (la contraseña se oculta) |

## Interconexión entre dispositivos

Dos o más celulares con la app se comunican por WiFi a través de Firebase:

1. El celular A presiona **Activar**. Se guarda `circuito/estado = ACTIVADO`.
2. El celular B, que está en "Manipular Circuito", ve el cambio al instante (`addValueEventListener`).
3. La acción queda guardada en `acciones` y aparece en "Ver Acciones" y "Estadísticas" de ambos.

En la Unidad 3 el ESP32 va a leer `circuito/estado` y a escribir en `acciones` los obstáculos que detecte el sensor.

## Estructura de la base de datos

```
usuarios
  └── 11111111-1
        rut, nombre, apellido, correo, cargo, contrasena (SHA-256)
circuito
  estado   : ACTIVADO / DESACTIVADO
  usuario  : rut de quien hizo el último cambio
acciones
  └── (clave push)
        fecha, hora, usuario, accion, distancia
```

## Seguridad (ISO/IEC 27400)

| Medida | Dónde |
|---|---|
| Contraseñas cifradas con hash **SHA-256** (nunca en texto plano) | `Seguridad.kt`, `CrearCuenta.kt`, `Principal.kt` |
| Validación de Rut, correo, largo mínimo de contraseña (6) y confirmación | `CrearCuenta.kt` |
| **Bloqueo de 30 s** después de 3 intentos fallidos de login | `Principal.kt` |
| La contraseña no se muestra en el perfil | `Perfil.kt` |
| Conexión segura: Firebase usa HTTPS/TLS y se bloquea el tráfico sin cifrar (`usesCleartextTraffic=false`) | `AndroidManifest.xml` |
| Sin respaldo automático de datos de la app (`allowBackup=false`) | `AndroidManifest.xml` |
| **Reglas de Firebase**: validan el formato de cada dato, no permiten sobrescribir usuarios ni borrar el historial | `database.rules.json` |
| Cerrar sesión limpia la pila de pantallas y se desactiva la flecha de retroceso | `Menu.kt` |

**Mejora pendiente:** usar Firebase Authentication para que las reglas exijan un usuario autenticado (`auth != null`).

## Cómo ejecutar

1. Clonar el repositorio y abrirlo en Android Studio.
2. Poner el archivo `google-services.json` de tu proyecto Firebase en la carpeta `app/`.
3. En Firebase → Realtime Database → **Reglas**, pegar el contenido de `database.rules.json` y publicar.
4. Ejecutar en dos emuladores o celulares y probar Activar / Desactivar.

`acciones_ejemplo.json` trae dos lecturas de ejemplo del sensor, para probar "Ver Acciones" y "Estadísticas" mientras no está el ESP32.

---
Andrés Felipe Parra Valenzuela — Analista Programador, INACAP Rancagua
