package com.example.rebornfinance.domain.model

enum class MovementType(val displayName: String) {
    EXPENSE("Gasto"),
    INCOME("Ingreso"),
    REFUND("Reembolso"),
    BALANCE_ADJUSTMENT("Ajuste de saldo")
}
