package com.example.rebornfinance.feature.smartinput

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rebornfinance.core.date.DateUtils
import com.example.rebornfinance.data.local.AppDatabase
import com.example.rebornfinance.data.repository.EnvelopeRepositoryImpl
import com.example.rebornfinance.data.repository.MovementRepositoryImpl
import com.example.rebornfinance.domain.model.Movement
import com.example.rebornfinance.domain.model.MovementType
import com.example.rebornfinance.domain.model.ParsedOperationProposal
import com.example.rebornfinance.domain.parser.LocalRuleBasedParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SmartInputViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val movementRepository = MovementRepositoryImpl(database.movementDao())
    private val envelopeRepository = EnvelopeRepositoryImpl(database, database.envelopeDao(), database.envelopeOperationDao())

    val inputText = MutableStateFlow("")
    val proposal = MutableStateFlow<ParsedOperationProposal?>(null)
    val statusMessage = MutableStateFlow<String?>(null)
    val isAiActive = MutableStateFlow(false) // False = Local Rule-based analyzer

    fun analyzeInput() {
        val text = inputText.value
        if (text.isBlank()) {
            statusMessage.value = "Introduce una frase para analizar."
            return
        }

        val result = LocalRuleBasedParser.parse(text)
        proposal.value = result
        statusMessage.value = "Frase analizada con analizador local determinista."
    }

    fun confirmAndSave(onSuccess: () -> Unit) {
        val prop = proposal.value ?: return
        val amount = prop.amountCents
        if (amount == null || amount <= 0L) {
            statusMessage.value = "Importe inválido para guardar."
            return
        }

        viewModelScope.launch {
            val now = DateUtils.getCurrentTimestamp()
            when (prop.operationType) {
                "EXPENSE" -> {
                    val mov = Movement(
                        type = MovementType.EXPENSE,
                        amountCents = amount,
                        description = prop.description,
                        category = prop.category,
                        date = prop.date,
                        createdAt = now,
                        updatedAt = now
                    )
                    movementRepository.insertMovement(mov)
                }
                "INCOME" -> {
                    val mov = Movement(
                        type = MovementType.INCOME,
                        amountCents = amount,
                        description = prop.description,
                        category = prop.category,
                        date = prop.date,
                        createdAt = now,
                        updatedAt = now
                    )
                    movementRepository.insertMovement(mov)
                }
                "REFUND" -> {
                    val mov = Movement(
                        type = MovementType.REFUND,
                        amountCents = amount,
                        description = prop.description,
                        category = prop.category,
                        date = prop.date,
                        createdAt = now,
                        updatedAt = now
                    )
                    movementRepository.insertMovement(mov)
                }
            }
            statusMessage.value = "¡Operación guardada correctamente!"
            onSuccess()
        }
    }

    fun cancelProposal() {
        proposal.value = null
        inputText.value = ""
        statusMessage.value = null
    }
}
