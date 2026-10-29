package com.example.promo70.ui.home

import com.example.promo70.model.PromoCategory
import com.example.promo70.model.PromoItem

data class PromoHomeUiState(
    val isLoading: Boolean = false,
    val items: List<PromoItem> = emptyList(),
    val selectedCategory: PromoCategory = PromoCategory.ALL,
    val searchQuery: String = "",
    val currentTab: Int = 0,
    val errorMessage: String? = null
) {
    val displayedItems: List<PromoItem>
        get() {
            var result = items

            // Filtrar por pestaña Favoritos si está activa
            if (currentTab == 1) {
                result = result.filter { it.isFavorite }
            }

            // Filtrar por categoría
            if (selectedCategory != PromoCategory.ALL) {
                result = result.filter { it.category == selectedCategory }
            }

            // Filtrar por texto de búsqueda
            if (searchQuery.isNotBlank()) {
                val query = searchQuery.trim().lowercase()
                result = result.filter {
                    it.title.lowercase().contains(query) ||
                    it.description.lowercase().contains(query)
                }
            }

            return result
        }

    val favoritesCount: Int
        get() = items.count { it.isFavorite }
}
