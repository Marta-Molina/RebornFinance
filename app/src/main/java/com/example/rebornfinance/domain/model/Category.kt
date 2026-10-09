package com.example.rebornfinance.domain.model

data class Category(
    val id: Long = 0L,
    val name: String,
    val type: MovementType,
    val isActive: Boolean = true
)
