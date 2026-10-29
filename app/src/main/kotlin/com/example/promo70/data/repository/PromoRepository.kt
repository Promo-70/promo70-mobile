package com.example.promo70.data.repository

import com.example.promo70.model.PromoItem
import kotlinx.coroutines.flow.Flow

interface PromoRepository {
    fun getPromotions(): Flow<List<PromoItem>>
    suspend fun toggleFavorite(promoId: String)
    suspend fun refresh()
}
