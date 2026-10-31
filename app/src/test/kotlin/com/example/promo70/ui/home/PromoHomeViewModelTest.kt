package com.example.promo70.ui.home

import com.example.promo70.data.repository.FakePromoRepository
import com.example.promo70.model.PromoCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PromoHomeViewModelTest {

    @Test
    fun uiState_loadsPromotionsSuccessfully() = runTest {
        val repository = FakePromoRepository()
        val viewModel = PromoHomeViewModel(repository)

        val state = viewModel.uiState.first { !it.isLoading }

        assertFalse(state.isLoading)
        assertTrue(state.items.isNotEmpty())
    }

    @Test
    fun uiState_filtersByCategory() = runTest {
        val repository = FakePromoRepository()
        val viewModel = PromoHomeViewModel(repository)

        viewModel.onCategorySelected(PromoCategory.TECH)

        val state = viewModel.uiState.first { !it.isLoading }
        assertTrue(state.displayedItems.all { it.category == PromoCategory.TECH })
    }

    @Test
    fun uiState_filtersBySearchQuery() = runTest {
        val repository = FakePromoRepository()
        val viewModel = PromoHomeViewModel(repository)

        viewModel.onSearchQueryChange("auriculares")

        val state = viewModel.uiState.first { !it.isLoading }
        assertTrue(state.displayedItems.all { it.title.lowercase().contains("auriculares") })
    }

    @Test
    fun uiState_toggleFavoriteUpdatesItem() = runTest {
        val repository = FakePromoRepository()
        val viewModel = PromoHomeViewModel(repository)

        val initialList = viewModel.uiState.first { !it.isLoading }.items
        val targetItem = initialList.first()

        viewModel.onToggleFavorite(targetItem.id)

        val updatedList = viewModel.uiState.first { state ->
            state.items.first { it.id == targetItem.id }.isFavorite != targetItem.isFavorite
        }.items

        val updatedItem = updatedList.first { it.id == targetItem.id }
        assertEquals(!targetItem.isFavorite, updatedItem.isFavorite)
    }
}
