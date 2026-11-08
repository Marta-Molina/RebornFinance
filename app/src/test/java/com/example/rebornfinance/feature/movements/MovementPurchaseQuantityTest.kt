package com.example.rebornfinance.feature.movements

import com.example.rebornfinance.core.validation.QuantityParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MovementPurchaseQuantityTest {

    @Test
    fun testExactDecimalParsing() {
        assertEquals(10000L, QuantityParser.parseThousandths("10").getOrNull())
        assertEquals(2500L, QuantityParser.parseThousandths("2,5").getOrNull())
        assertEquals(2500L, QuantityParser.parseThousandths("2.5").getOrNull())
        assertEquals(1L, QuantityParser.parseThousandths("0,001").getOrNull())
        assertEquals(50000L, QuantityParser.parseThousandths("50").getOrNull())
    }

    @Test
    fun testRejectionOfInvalidQuantities() {
        assertTrue(QuantityParser.parseThousandths("").isFailure)
        assertTrue(QuantityParser.parseThousandths("0").isFailure)
        assertTrue(QuantityParser.parseThousandths("-2,5").isFailure)
        assertTrue(QuantityParser.parseThousandths("abc").isFailure)
        assertTrue(QuantityParser.parseThousandths("1,2345").isFailure) // > 3 decimals
        assertTrue(QuantityParser.parseThousandths("1..5").isFailure) // multiple separators
    }

    @Test
    fun testIntegerParsing() {
        assertEquals(2L, QuantityParser.parseInteger("2").getOrNull())
        assertTrue(QuantityParser.parseInteger("0").isFailure)
        assertTrue(QuantityParser.parseInteger("-1").isFailure)
        assertTrue(QuantityParser.parseInteger("abc").isFailure)
    }

    @Test
    fun testFormatThousandths() {
        assertEquals("10", QuantityParser.formatThousandths(10000L))
        assertEquals("2,5", QuantityParser.formatThousandths(2500L))
        assertEquals("0,001", QuantityParser.formatThousandths(1L))
        assertEquals("0,125", QuantityParser.formatThousandths(125L))
        assertEquals("0", QuantityParser.formatThousandths(0L))
        assertEquals("-2,5", QuantityParser.formatThousandths(-2500L))
    }
}
