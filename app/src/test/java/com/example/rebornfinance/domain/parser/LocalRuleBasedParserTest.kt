package com.example.rebornfinance.domain.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class LocalRuleBasedParserTest {

    @Test
    fun testParseExpense() {
        val proposal = LocalRuleBasedParser.parse("He gastado 35 euros en silicona")
        assertEquals("EXPENSE", proposal.operationType)
        assertEquals(3500L, proposal.amountCents)
        assertEquals("Silicona", proposal.description)
    }

    @Test
    fun testParseSale() {
        val proposal = LocalRuleBasedParser.parse("Ayer vendí el reborn Lucía por 450 euros")
        assertEquals("INCOME", proposal.operationType)
        assertEquals(45000L, proposal.amountCents)
        assertEquals("Lucía", proposal.projectName)
    }

    @Test
    fun testParseRefund() {
        val proposal = LocalRuleBasedParser.parse("Me han devuelto 12,50 euros de un pedido")
        assertEquals("REFUND", proposal.operationType)
        assertEquals(1250L, proposal.amountCents)
    }
}
