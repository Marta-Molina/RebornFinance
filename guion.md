# Guion y Resumen de Desarrollo — RebornFinance

Este documento recoge el resumen completo del desarrollo, evolución, decisiones arquitectónicas, correcciones de errores y estado actual de la aplicación **RebornFinance** (Gestor financiero offline-first y taller de muñecas reborn).

---

## 1. Stack Tecnológico y Arquitectura
* **Lenguaje**: Kotlin 2.0.21
* **UI**: Jetpack Compose, Material 3
* **Arquitectura**: MVVM con separación estricta en Clean Architecture (`core`, `data/local`, `domain/model/calculator/parser`, `feature/ui`).
* **Persistencia**: Room Database (versión actual: **13**, con migraciones acumuladas 1→13), DataStore (preferencias).
* **Convención Monetaria**: Todos los importes monetarios se almacenan exclusivamente como enteros en céntimos (`Long`), evitando tipos flotantes para evitar imprecisiones financieras.

---

## 2. Resumen Histórico por Fases

### Fases 1 a 5: Núcleo Funcional e Inventario Inicial
* **Fase 1**: Configuración inicial de movimientos financieros, categorías, saldo inicial, base de datos Room y primeras pruebas unitarias.
* **Fase 2**: Creación de proyectos reborn, seguimiento de costes de fabricación y registro de ventas.
* **Fase 3**: Inventario de materiales y lotes de compra mediante valoración FIFO, con reglas automáticas de consumo por tamaño (pulgadas del kit).
* **Fase 4**: Inventario especializado de ojos (pares/unidades) y pelo (gramos), asignación a proyectos y cancelación/devolución de stock.
* **Fase 5**: Sobres virtuales, presupuestos y objetivos de ahorro.

### Fases 6 a 8: Informes, Parser y Auditoría
* **Fase 6**: Estadísticas financieras con filtros de fechas, exportación a CSV e informes nativos en PDF (`PdfReportUtils`) con `FileProvider`.
* **Fase 7**: Analizador local determinista de lenguaje natural (`LocalRuleBasedParser`) en español para interpretar compras, gastos, ventas y ahorros mediante texto.
* **Fase 8**: Auditoría integral, seguridad de intents, copias de seguridad locales y sincronización con GitHub.

### Fases 9 a 9.7M: Reestructuración Profunda del Catálogo y Sincronización
* **Fases 9 - 9.3**:
  * Adición del selector de sistema de pintura por proyecto (*Termosellable / Heat-set* vs *Secado al aire / Air-dry*).
  * Resolución del error de reglas de consumo para proyectos de 18 pulgadas mediante siembra automática en migraciones Room y fallbacks robustos en repositorios.
  * Consolidación del catálogo maestro de **13 familias y subcategorías** (Kits, Ojos, Pelo, Imprimaciones, Pinturas, Barnices, Rellenos, Adhesivos, Herramientas, Agujas, Ropa, Embalaje, Otros).
* **Fases 9.4 - 9.7M (Resolución de Incidencias y Auditorías)**:
  * **Eliminación de existencias ficticias**: Se eliminó el código que auto-creaba materiales estándar ("estándar de 50ml") sin una compra real previa.
  * **Separación Estricta de Catálogos**: Se separaron las **categorías financieras** (`EXPENSE`, `INCOME`, `REFUND` en la tabla `categories`) del **catálogo técnico de inventario** (`InventoryCatalog.masterCatalog`).
  * **Analizador de Cantidades Exactas (`QuantityParser.kt`)**: Se eliminó completamente el uso de `Double`, `toDoubleOrNull()` y `round()`. Se implementó un parser basado en análisis estricto de caracteres y aritmética entera (`Long`) para manejar milésimas (gramos) y enteros (unidades/pares) con control de desbordamiento.
  * **Navegación y UI**: Integración del cajón lateral izquierdo (`ModalNavigationDrawer`) para navegar por las 13 familias de inventario, cuadrícula de inventario de 2 tarjetas por fila, formularios con cascada de subcategorías y un diálogo popup para añadir materiales desde el inventario en los proyectos reborn.
  * **Pruebas y Migraciones Room**: Actualización del esquema hasta la versión 13 con migraciones idempotentes (`MIGRATION_12_13`), pruebas unitarias de cantidad (22 pruebas pasadas) y pruebas instrumentadas de migración con `MigrationTestHelper`.

---

## 3. Estado Actual del Repositorio
* **Rama**: `main`
* **Compilación**: Limpia y exitosa (`Build finished successfully.`).
* **Pruebas**: 22 pruebas unitarias superadas con éxito (0 fallos).
* **APK Generado**: `app/build/outputs/apk/debug/app-debug.apk` (`applicationId`: `com.example.rebornfinance`).

---
*Este documento queda guardado localmente en el repositorio como `guion.md` para facilitar la continuidad del desarrollo al cambiar de entorno u ordenador.*
