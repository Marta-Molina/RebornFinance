package com.example.rebornfinance.domain.model

data class RebornProject(
    val id: Long = 0L,
    val name: String,
    val kitName: String,
    val sculptorName: String,
    val sizeInches: Double,
    val startDate: Long,
    val status: ProjectStatus,
    val predictedSalePriceCents: Long? = null,
    val actualSalePriceCents: Long? = null,
    val saleDate: Long? = null,
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)
