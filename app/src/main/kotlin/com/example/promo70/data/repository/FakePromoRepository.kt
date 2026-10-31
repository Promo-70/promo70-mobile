package com.example.promo70.data.repository

import com.example.promo70.model.PromoCategory
import com.example.promo70.model.PromoItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakePromoRepository : PromoRepository {

    private val initialPromos = listOf(
        PromoItem(
            id = "1",
            title = "Auriculares Inalámbricos Pro",
            description = "Cancelación activa de ruido, batería de 30 horas y sonido espacial.",
            price = 49.99,
            originalPrice = 99.99,
            discountPercent = 50,
            category = PromoCategory.TECH,
            rating = 4.9f,
            tag = "MÁS VENDIDO"
        ),
        PromoItem(
            id = "2",
            title = "Smartwatch Deportivo X",
            description = "Monitor de ritmo cardíaco, resistencia al agua y GPS integrado.",
            price = 69.50,
            originalPrice = 139.00,
            discountPercent = 50,
            category = PromoCategory.TECH,
            rating = 4.7f,
            tag = "OFERTA FLASH"
        ),
        PromoItem(
            id = "3",
            title = "Zapatillas Running Ultra",
            description = "Amortiguación reactiva ideal para entrenamientos de alto impacto.",
            price = 59.90,
            originalPrice = 120.00,
            discountPercent = 50,
            category = PromoCategory.SPORTS,
            rating = 4.8f,
            tag = "TENDENCIA"
        ),
        PromoItem(
            id = "4",
            title = "Cafetera Espresso Automática",
            description = "15 bares de presión con espumador de leche para capuchinos perfectos.",
            price = 89.99,
            originalPrice = 149.99,
            discountPercent = 40,
            category = PromoCategory.HOME,
            rating = 4.6f
        ),
        PromoItem(
            id = "5",
            title = "Campera Impermeable Térmica",
            description = "Material transpirable cortaviento para climas fríos y senderismo.",
            price = 45.00,
            originalPrice = 90.00,
            discountPercent = 50,
            category = PromoCategory.FASHION,
            rating = 4.5f
        ),
        PromoItem(
            id = "6",
            title = "Combo Hamburguesas Gourmet x2",
            description = "Incluye papas rústicas y bebidas artesanales de cortesía.",
            price = 14.99,
            originalPrice = 24.99,
            discountPercent = 40,
            category = PromoCategory.FOOD,
            rating = 4.9f,
            tag = "COMBO DÚO"
        )
    )

    private val _promotions = MutableStateFlow(initialPromos)

    override fun getPromotions(): Flow<List<PromoItem>> = _promotions.asStateFlow()

    override suspend fun toggleFavorite(promoId: String) {
        _promotions.update { currentList ->
            currentList.map { item ->
                if (item.id == promoId) {
                    item.copy(isFavorite = !item.isFavorite)
                } else {
                    item
                }
            }
        }
    }

    override suspend fun refresh() {
        // Simular delay de red
        delay(800)
    }
}
