package com.example.rebornfinance.core.money

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoneyUtilsTest {

    @Test
    fun testFormatCents() {
        assertEquals("0,00 €", MoneyUtils.formatCents(0L))
        assertEquals("0,99 €", MoneyUtils.formatCents(99L))
        assertEquals("1,00 €", MoneyUtils.formatCents(100L))
        assertEquals("12,50 €", MoneyUtils.formatCents(1250L))
        assertEquals("1.250,50 €", MoneyUtils.formatCents(125050L))
        assertEquals("1.000.000,00 €", MoneyUtils.formatCents(100000000L))
        assertEquals("-5,50 €", MoneyUtils.formatCents(-550L))
    }

    @Test
    fun testParseAmountToCents_SpanishFormat() {
        assertEquals(1250L, MoneyUtils.parseAmountToCents("12,50"))
        assertEquals(99L, MoneyUtils.parseAmountToCents("0,99"))
        assertEquals(1200L, MoneyUtils.parseAmountToCents("12"))
        assertEquals(1250L, MoneyUtils.parseAmountToCents("12,5"))
        assertEquals(125050L, MoneyUtils.parseAmountToCents("1.250,50"))
        assertEquals(150000L, MoneyUtils.parseAmountToCents("1.500"))
        assertEquals(3500L, MoneyUtils.parseAmountToCents("35,00 €"))
        assertEquals(5000L, MoneyUtils.parseAmountToCents(" 50 € "))
    }

    @Test
    fun testParseAmountToCents_DotFormat() {
        assertEquals(1250L, MoneyUtils.parseAmountToCents("12.50"))
        assertEquals(99L, MoneyUtils.parseAmountToCents("0.99"))
        assertEquals(1250L, MoneyUtils.parseAmountToCents("12.5"))
        assertEquals(125050L, MoneyUtils.parseAmountToCents("1,250.50"))
    }

    @Test
    fun testParseAmountToCents_InvalidFormats() {
        assertNull(MoneyUtils.parseAmountToCents(""))
        assertNull(MoneyUtils.parseAmountToCents("   "))
        assertNull(MoneyUtils.parseAmountToCents("abc"))
        assertNull(MoneyUtils.parseAmountToCents("12,345")) // More than 2 decimal places not allowed
        assertNull(MoneyUtils.parseAmountToCents("12.345.678,99,00"))
    }
}
