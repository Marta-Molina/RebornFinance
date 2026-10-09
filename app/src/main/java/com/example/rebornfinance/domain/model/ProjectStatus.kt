package com.example.rebornfinance.domain.model

enum class ProjectStatus(val displayName: String) {
    PLANNED("Planificado"),
    IN_PROCESS("En proceso"),
    FINISHED("Terminado"),
    SOLD("Vendido"),
    CANCELLED("Cancelado")
}
