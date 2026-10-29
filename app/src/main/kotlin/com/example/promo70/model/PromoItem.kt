package com.example.promo70.model

data class PromoItem(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val originalPrice: Double,
    val discountPercent: Int,
    val category: PromoCategory,
    val rating: Float = 4.8f,
    val isFavorite: Boolean = false,
    val tag: String? = null
)
