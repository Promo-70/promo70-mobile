package com.example.promo70.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.promo70.data.repository.FakePromoRepository
import com.example.promo70.data.repository.PromoRepository
import com.example.promo70.model.PromoCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PromoHomeViewModel(
    private val repository: PromoRepository = FakePromoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromoHomeUiState(isLoading = true))
    val uiState: StateFlow<PromoHomeUiState> = _uiState.asStateFlow()

    init {
        loadPromotions()
    }

    private fun loadPromotions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getPromotions()
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.localizedMessage ?: "Error al cargar promociones"
                        )
                    }
                }
                .collect { list ->
                    _uiState.update {
                        it.copy(
                            items = list,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
    }

    fun onClearSearch() {
        _uiState.update { it.copy(searchQuery = "") }
    }

    fun onCategorySelected(category: PromoCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onTabSelected(tabIndex: Int) {
        _uiState.update { it.copy(currentTab = tabIndex) }
    }

    fun onToggleFavorite(id: String) {
        viewModelScope.launch {
            repository.toggleFavorite(id)
        }
    }

    fun onRefresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.refresh()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
