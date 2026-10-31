# Promo70 Mobile - Arquitectura MVVM con Jetpack Compose

Maquetado base de una aplicación Android moderna desarrollada en **Kotlin** utilizando **Jetpack Compose** (Material 3) y el patrón de arquitectura **MVVM (Model-View-ViewModel)**.

---

## 🏛️ Estructura del Proyecto y Capas MVVM

```
app/src/main/kotlin/com/example/promo70/
├── model/                               # 📦 MODEL (Entidades de dominio)
│   ├── PromoItem.kt                     # Modelo de datos de la promoción/producto
│   └── PromoCategory.kt                 # Enumeración de categorías
│
├── data/                                # 💾 DATA LAYER (Datos y repositorios)
│   └── repository/
│       ├── PromoRepository.kt           # Contrato/Interface del repositorio
│       └── FakePromoRepository.kt       # Implementación simulada reactiva (Flow/StateFlow)
│
├── ui/                                  # 🎨 VIEW & VIEWMODEL
│   ├── components/                      # Componentes reutilizables de UI
│   │   ├── PromoCard.kt                 # Tarjeta con información, descuentos y favoritos
│   │   ├── PromoSearchBar.kt            # Barra de búsqueda con limpieza de texto
│   │   ├── CategoryFilterChips.kt       # Filtros horizontales por categoría
│   │   └── PromoBottomBar.kt            # Barra de navegación inferior con badges
│   │
│   ├── home/                            # Feature Home
│   │   ├── PromoHomeUiState.kt          # Estado inmutable de la pantalla (UiState)
│   │   ├── PromoHomeViewModel.kt        # ViewModel (gestión de estado y eventos)
│   │   └── PromoHomeScreen.kt           # Composables Stateful y Stateless + Previews
│   │
│   └── theme/                           # Estilos, colores y tipografía Material 3
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
│
├── Navigation.kt                        # Configuración de navegación con Navigation 3
└── MainActivity.kt                      # Punto de entrada de la aplicación
```

---

## 🧩 Implementación del Patrón MVVM

### 1. Modelo (`Model`)
- **[`PromoItem`](file:///C:/Users/EQUIPO/Documents/promo70-mobile/app/src/main/kotlin/com/example/promo70/model/PromoItem.kt)**: Representa el elemento de oferta con propiedades como título, descripción, precios, porcentaje de descuento, categoría, calificación y estado de favorito.
- **[`PromoRepository`](file:///C:/Users/EQUIPO/Documents/promo70-mobile/app/src/main/kotlin/com/example/promo70/data/repository/PromoRepository.kt)** y **[`FakePromoRepository`](file:///C:/Users/EQUIPO/Documents/promo70-mobile/app/src/main/kotlin/com/example/promo70/data/repository/FakePromoRepository.kt)**: Gestionan el flujo asíncrono de datos (`Flow<List<PromoItem>>`) y operaciones como actualizar favoritos o refrescar datos.

### 2. ViewModel (`ViewModel`)
- **[`PromoHomeViewModel`](file:///C:/Users/EQUIPO/Documents/promo70-mobile/app/src/main/kotlin/com/example/promo70/ui/home/PromoHomeViewModel.kt)**:
  - Expone un flujo de estado inmutable `uiState: StateFlow<PromoHomeUiState>`.
  - Procesa eventos del usuario (cambio en búsqueda, selección de categoría, cambio de pestaña, toggle de favorito y pull-to-refresh).
  - Maneja la concurrencia a través de `viewModelScope`.

### 3. Vista (`View` con Jetpack Compose)
- **[`PromoHomeScreen`](file:///C:/Users/EQUIPO/Documents/promo70-mobile/app/src/main/kotlin/com/example/promo70/ui/home/PromoHomeScreen.kt)**:
  - **Stateful**: Conecta el `ViewModel` mediante `collectAsStateWithLifecycle()` y propaga eventos.
  - **Stateless**: Recibe únicamente el estado (`PromoHomeUiState`) y callbacks (Unidirectional Data Flow - UDF).
  - Manejo integral de estados: Carga (`Loading`), Error (`Error`), Vacío (`Empty`) y Lista con datos (`Success`).
  - Navegación inferior con pestañas (Inicio, Favoritos con contador en badge, Perfil).
