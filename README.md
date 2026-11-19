# CaducApp (control de caducidad de alimentos)
 
**Problema que resuelve:** en casa se tira mucha comida porque se olvida qué hay en la nevera y cuándo caduca. La app registra los alimentos con su fecha de caducidad, los ordena por urgencia y avisa antes de que se estropeen.
 
## Por qué encaja con las reglas del proyecto
 
- Resuelve un problema cotidiano real (desperdicio de comida y dinero).
- Usa Java y XML en Android Studio.
- Tiene suficiente contenido técnico para explicar en 5 minutos: base de datos, lista dinámica, fechas, notificaciones y lógica de colores.
---
 
## Prompt para generar el proyecto
 
Copiar tal cual en el asistente de IA (ChatGPT, Claude, Gemini...):
 
```
Actúa como un desarrollador Android senior y mentor. Quiero crear una app
llamada "CaducApp" en Android Studio usando EXCLUSIVAMENTE Java (no Kotlin)
y layouts XML (no Jetpack Compose).
 
OBJETIVO DE LA APP
Ayudar a las personas a no desperdiciar comida: registrar alimentos con su
fecha de caducidad, mostrarlos ordenados por urgencia y avisar con una
notificación antes de que caduquen.
 
REQUISITOS TÉCNICOS
- minSdk 24, targetSdk 34, lenguaje Java, Gradle (Groovy o Kotlin DSL).
- Librerías: Room (base de datos local), RecyclerView, Material Components,
  WorkManager (notificaciones diarias).
- Arquitectura sencilla: Activity + ViewModel + Repository + Room (MVVM).
- Sin conexión a internet, todo local.
- SIMPLE Y SENCILLA
 
FUNCIONALIDADES
1. Pantalla principal con RecyclerView de alimentos ordenados por fecha de
   caducidad (los más urgentes primero).
2. Cada elemento muestra: nombre, categoría, cantidad, fecha de caducidad y
   los días restantes, con un indicador de color:
   - Rojo: caducado o caduca en 0-2 días
   - Naranja: caduca en 3-5 días
   - Verde: más de 5 días
3. Botón flotante (FAB) para añadir un alimento: formulario con nombre,
   categoría (Spinner: Lácteos, Carne, Pescado, Verdura, Fruta, Congelado,
   Otros), cantidad y fecha (DatePickerDialog).
4. Editar un alimento al pulsarlo y eliminarlo con deslizamiento (swipe) o
   con botón, con confirmación.
5. Filtro por categoría (chips o Spinner) y buscador por nombre.
6. Botón/acción "Marcar como consumido" que lo elimina de la lista y suma a
   un contador de "alimentos aprovechados".
7. Pantalla de estadísticas simple: alimentos aprovechados vs. alimentos
   caducados.
8. Notificación diaria (WorkManager, a las 9:00) que avise de los alimentos
   que caducan en 2 días o menos. Pedir permiso POST_NOTIFICATIONS en
   Android 13+.
 
ESTRUCTURA DE PAQUETES
com.ejemplo.caducapp
 ├─ data (entidad Alimento, AlimentoDao, AppDatabase, Repository)
 ├─ ui (MainActivity, AddEditActivity, EstadisticasActivity, adaptador)
 ├─ viewmodel
 └─ notifications (Worker y creación del canal)
 
FORMA DE TRABAJAR (importante)
- Entrégame el proyecto POR FASES, no todo de golpe, y espera a que yo
  confirme que compila antes de pasar a la siguiente:
  Fase 1: configuración de Gradle, dependencias y entidad/DAO/base de datos.
  Fase 2: pantalla principal con RecyclerView y adaptador.
  Fase 3: formulario de añadir/editar con DatePicker.
  Fase 4: filtros, búsqueda, swipe y "marcar como consumido".
  Fase 5: estadísticas.
  Fase 6: notificaciones con WorkManager y permisos.
- Para cada fase indica la ruta exacta de cada archivo, el código completo
  (no fragmentos sueltos), los layouts XML y los cambios en AndroidManifest.
- Comenta el código en español de forma clara, ya que tendré que explicarlo
  en un vídeo.
- Al final de cada fase, resume en 3-4 líneas qué hace cada clase creada.
- Si cometo un error de compilación y te pego el log, explícame la causa
  antes de darme la corrección.
 
Empieza solo con la Fase 1.
```
 
---
 
## Cómo sacarle partido para el vídeo (5 min)
 
Guardar un **registro de la IA** mientras se trabaja, ejemplo:
 
| Qué anotar      | Ejemplo                                                        |
|-----------------|----------------------------------------------------------------|
| Prompt usado    | "Fase 3: formulario con DatePicker"                            |
| Fallo detectado | Error de compilación en Room, olvidar el `annotationProcessor` |
| Cómo lo corregí | Pegué el log a la IA y añadí la dependencia que faltaba        |
 
### Guion orientativo del vídeo
 
| Tiempo    | Contenido                                                                                                                       |
|-----------|---------------------------------------------------------------------------------------------------------------------------------|
| 0:00-0:30 | Presentación personal y problema que resuelve                                                                                   |
| 0:30-1:45 | Demo en vivo (añadir, colores, filtro, consumir, notificación)                                                                  |
| 1:45-3:15 | Código: Entity/DAO, Adapter, lógica de colores por días, Worker                                                                 |
| 3:15-4:15 | Uso de la IA: prompts, 2 o 3 errores reales y su solución                                                                       |
| 4:15-5:00 | Aprendizajes, limitaciones y mejoras (escáner de código de barras, sincronización en la nube, recetas con lo que caduca pronto) |
 
### Consejos
 
- Probar la notificación antes de grabar. Para la demo, cambiar el Worker a una ejecución inmediata, porque esperar a las 9:00 no es viable.
- Mostrar cara y voz en la grabación, como piden las instrucciones.
- Subir el proyecto a GitHub con un README corto para la entrega.
---
 
## Checklist de entrega
 
- [ ] Código fuente (repositorio GitHub o proyecto ejecutable)
- [ ] Vídeo de máximo 5 minutos con rostro y voz
- [ ] Presentación personal y justificación de la app
- [ ] Demo en vivo funcionando
- [ ] Recorrido por el código y estructuras principales
- [ ] Detalle del uso de la IA (prompts, errores y correcciones)
- [ ] Aprendizajes, limitaciones y mejoras futuras
 