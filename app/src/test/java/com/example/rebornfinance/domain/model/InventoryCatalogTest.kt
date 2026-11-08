package com.example.rebornfinance.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InventoryCatalogTest {

    @Test
    fun testMasterCatalogHasExact13Families() {
        val expectedFamilies = listOf(
            "Kits y esculturas",
            "Ojos",
            "Pelo y cabello",
            "Imprimaciones y preparación",
            "Pinturas y pigmentos",
            "Barnices y acabados",
            "Rellenos y materiales internos",
            "Adhesivos y consumibles auxiliares",
            "Herramientas reutilizables",
            "Agujas y accesorios de injerto",
            "Ropa y accesorios de bebé",
            "Embalaje y envíos",
            "Otros materiales"
        )
        assertEquals(13, InventoryCatalog.masterCatalog.size)
        assertEquals(expectedFamilies, InventoryCatalog.masterCatalog.keys.toList())
    }

    @Test
    fun testKitsSubcategoriesMatchSection4() {
        val kits = InventoryCatalog.masterCatalog["Kits y esculturas"]!!
        val expected = listOf(
            "Edición limitada",
            "Edición abierta",
            "Cabezas solamente",
            "Brazos solamente",
            "Piernas solamente",
            "Cuerpos de tela",
            "Cuerpos de vinilo",
            "Belly plates",
            "Otros cuerpos y piezas"
        )
        assertEquals(expected, kits)
    }

    @Test
    fun testEyesSubcategoriesMatchSection4() {
        val eyes = InventoryCatalog.masterCatalog["Ojos"]!!
        val expected = listOf(
            "Ojos de cristal",
            "Ojos de resina",
            "Ojos de plástico",
            "Ojos de otros materiales",
            "Otros"
        )
        assertEquals(expected, eyes)
    }

    @Test
    fun testHairSubcategoriesMatchSection4() {
        val hair = InventoryCatalog.masterCatalog["Pelo y cabello"]!!
        val expected = listOf(
            "Mohair",
            "Pelo humano",
            "Sintético",
            "Alpaca",
            "Otros tipos de pelo",
            "Pelucas y piezas preparadas"
        )
        assertEquals(expected, hair)
    }

    @Test
    fun testPrimersSubcategoriesMatchSection4() {
        val primers = InventoryCatalog.masterCatalog["Imprimaciones y preparación"]!!
        val expected = listOf(
            "Imprimación para vinilo",
            "Imprimación para silicona",
            "Otros"
        )
        assertEquals(expected, primers)
    }

    @Test
    fun testPaintsSubcategoriesMatchSection4() {
        val paints = InventoryCatalog.masterCatalog["Pinturas y pigmentos"]!!
        val expected = listOf(
            "Acrílicas de secado al aire",
            "Pinturas para silicona",
            "Pinturas termosellables o heat-set",
            "Pigmentos en polvo",
            "Otras"
        )
        assertEquals(expected, paints)
    }

    @Test
    fun testVarnishesSubcategoriesMatchSection4() {
        val varnishes = InventoryCatalog.masterCatalog["Barnices y acabados"]!!
        val expected = listOf(
            "Mate",
            "Satinado",
            "Brillante",
            "Texturizador",
            "Otros acabados"
        )
        assertEquals(expected, varnishes)
    }

    @Test
    fun testFillingsSubcategoriesMatchSection4() {
        val fillings = InventoryCatalog.masterCatalog["Rellenos y materiales internos"]!!
        val expected = listOf(
            "Guata",
            "Discos de fieltro",
            "Vidrio triturado",
            "Microesferas de vidrio",
            "Microesferas de plástico",
            "Discos de microacero",
            "Otros"
        )
        assertEquals(expected, fillings)
    }

    @Test
    fun testNeedleVariantsMatchSection4() {
        assertEquals(8, InventoryCatalog.needleSizes.size)
        assertTrue(InventoryCatalog.needleSizes.contains("36G, 3 barbas"))
        assertTrue(InventoryCatalog.needleSizes.contains("40G, 3 barbas"))
        assertTrue(InventoryCatalog.needleSizes.contains("42G, 3 barbas"))
        assertTrue(InventoryCatalog.needleSizes.contains("42G, 1-2 barbas"))
        assertTrue(InventoryCatalog.needleSizes.contains("42G, 1 barba"))
        assertTrue(InventoryCatalog.needleSizes.contains("43G, 6 muescas"))
        assertTrue(InventoryCatalog.needleSizes.contains("43G, 1 barba"))
        assertTrue(InventoryCatalog.needleSizes.contains("46G, 6 barbas"))
    }

    @Test
    fun testValidUnitsDefinedForEachFamily() {
        for (family in InventoryCatalog.masterCatalog.keys) {
            val units = InventoryCatalog.validUnitsPerFamily[family]
            assertTrue("Familia '$family' debe tener unidades válidas definidas", !units.isNullOrEmpty())
        }
    }
}
