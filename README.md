# CORPALIV

Aplicación móvil Android desarrollada para la asignatura **DSY1105 Desarrollo de Aplicaciones Móviles**, en el contexto de CORPALIV y su Red Sociolaboral Alba Lab.

El proyecto presenta información institucional, productos y servicios, y un flujo de donaciones simuladas. Integra formularios validados, navegación, almacenamiento local, cámara, galería y animaciones mediante Kotlin y Jetpack Compose.

## Integrantes

- Benjamín Mora.
- Eydan Pérez.

## Funcionalidades implementadas

- Navegación mediante menú lateral entre Inicio, Nosotros, Productos, Donaciones, Historial y Notas.
- Pantallas informativas de CORPALIV y ejemplos de productos y servicios de Alba Lab.
- Formulario de donaciones con nombre, correo, comuna, monto, fecha, datos de tarjeta de prueba y aceptación de términos.
- Validaciones de campos obligatorios, formato de correo, comuna, rango del monto, fecha, número de tarjeta, vencimiento y CVV.
- Búsqueda de comunas con sugerencias que ignoran mayúsculas y tildes.
- Mensajes e iconos de error, indicador de procesamiento y confirmación del registro.
- Persistencia local de donaciones e historial con cantidad, total y eliminación de registros.
- Notas con título y contenido, validación, almacenamiento local, búsqueda y eliminación con confirmación.
- Foto de perfil seleccionada desde la galería o capturada con la cámara, conservada en almacenamiento interno.
- Gestión del permiso de cámara y uso de FileProvider para compartir el destino temporal de la captura.
- Adaptación del contenido al ancho disponible, con una o dos columnas en el formulario de donaciones.
- Transiciones entre pantallas, animaciones del menú y retroalimentación al presionar botones.

Las donaciones son una **simulación académica**: no se realizan cobros ni se envían comprobantes por correo. El número de tarjeta, el vencimiento y el CVV no se guardan en la tabla de donaciones. Se deben utilizar datos de prueba ficticios.

## Tecnologías

| Tecnología | Uso |
|---|---|
| Kotlin | Lenguaje de programación |
| Jetpack Compose y Material 3 | Interfaz y componentes visuales |
| ViewModel y StateFlow | Gestión del estado y actualización de la interfaz |
| Corrutinas | Operaciones asíncronas |
| Room y SQLite | Persistencia de donaciones y notas |
| KSP | Generación del código de Room |
| Activity Result API | Cámara, selector de imágenes y permisos |
| FileProvider | Acceso al archivo temporal de la cámara |
| Gradle | Configuración y compilación |

## Organización del código

El paquete principal se encuentra en `app/src/main/java/com/example/miapp`.

| Ubicación | Responsabilidad |
|---|---|
| `MainActivity.kt` | Entrada de la aplicación y carga de Compose |
| `ui/App.kt` y `ui/Pantalla.kt` | Navegación y pantallas disponibles |
| `ui/screens` | Pantallas de la aplicación |
| `ui/components` | Campos, menú, foto de perfil y componentes reutilizables |
| `ui/theme` | Colores y tipografía |
| `viewmodel` | Estados, acciones y validadores |
| `data/model` | Formularios, errores y entidades |
| `data/repository` | Acceso y preparación de datos |
| `data/local` | Room, DAO, migraciones y catálogo de comunas |
| `util` | Formatos de fechas y montos |

La aplicación sigue una organización MVVM. Las pantallas observan el estado de los ViewModels, los validadores aplican las reglas y los repositorios acceden al almacenamiento. Room conserva donaciones y notas en `miapp.db`; la foto de perfil se guarda como archivo interno.

## Requisitos para ejecutar

- Android Studio compatible con la configuración Kotlin, Compose y Gradle del proyecto.
- Android SDK Platform 36 instalado desde SDK Manager.
- JDK de Gradle compatible con los plugins del proyecto, configurado en Android Studio.
- Emulador o dispositivo Android con API 24 o superior.
- Conexión a internet para descargar Gradle y las dependencias durante la primera sincronización.
- Para probar la captura, un dispositivo o emulador con cámara configurada y una aplicación capaz de atenderla.

La configuración utiliza Gradle 8.13, Android Gradle Plugin 8.12.3, Kotlin 2.0.21 y Room 2.6.1. El SDK mínimo es 24 y los SDK de compilación y destino son 36.
