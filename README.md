# Recipes App

Es una app diseñada utilizando el patrón de arquitectura MVVM con una capa de dominio (Use Cases) intermedia. Hace un uso fuerte de Flow para la entrega continua de datos desde el Repositorio hasta el ViewModel.

## Detalles a resaltar
- La aplicación soporta caché, por lo tanto, solo cargará la información del servidor la primera vez que se ejecuta. A pesar de esto, también cuenta con la funcionalidad de "Swipe to refresh" en cada pantalla.
- El manejo de errores también es soportado con mensajes apropiados para cada tipo de error. Si ocurre un error en la primera carga de datos, aparecerá una pantalla de error con un botón de "Reintentar", si el error ocurre con datos cargados previamente, esta seguirá mostrando los datos y mostrará un snack-bar con el error.
- A pesar de que la aplicación soporta tanto el idioma Inglés como el Español, todas las recetas se encuentran en idioma inglés
- Las recetas tienen su formato un poco inconsistente, ya que se obtuvieron de una API libre

## Tech Stack utilizado

### En app
- Jetpack Compose (Material Design 3)
- Kotlin Coroutines - Flow
- Retrofit2 (Consultas al API)
- Jetpack Room (Cache)
- Hilt
- OSMDroid (Mapas)

### Testing
- JUnit4
- Mockk
- Kluent
