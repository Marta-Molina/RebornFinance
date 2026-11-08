package com.example.rebornfinance.domain.model

object InventoryCatalog {
    val masterCatalog: Map<String, List<String>> = mapOf(
        "Kits y esculturas" to listOf(
            "Edición limitada",
            "Edición abierta",
            "Cabezas solamente",
            "Brazos solamente",
            "Piernas solamente",
            "Cuerpos de tela",
            "Cuerpos de vinilo",
            "Belly plates",
            "Otros cuerpos y piezas"
        ),
        "Ojos" to listOf(
            "Ojos de cristal",
            "Ojos de resina",
            "Ojos de plástico",
            "Ojos de otros materiales",
            "Otros"
        ),
        "Pelo y cabello" to listOf(
            "Mohair",
            "Pelo humano",
            "Sintético",
            "Alpaca",
            "Otros tipos de pelo",
            "Pelucas y piezas preparadas"
        ),
        "Imprimaciones y preparación" to listOf(
            "Imprimación para vinilo",
            "Imprimación para silicona",
            "Otros"
        ),
        "Pinturas y pigmentos" to listOf(
            "Acrílicas de secado al aire",
            "Pinturas para silicona",
            "Pinturas termosellables o heat-set",
            "Pigmentos en polvo",
            "Otras"
        ),
        "Barnices y acabados" to listOf(
            "Mate",
            "Satinado",
            "Brillante",
            "Texturizador",
            "Otros acabados"
        ),
        "Rellenos y materiales internos" to listOf(
            "Guata",
            "Discos de fieltro",
            "Vidrio triturado",
            "Microesferas de vidrio",
            "Microesferas de plástico",
            "Discos de microacero",
            "Otros"
        ),
        "Adhesivos y consumibles auxiliares" to listOf(
            "Adhesivos para pelo y pestañas",
            "Pegamento para imanes",
            "Disolventes",
            "Cintas",
            "Imanes",
            "Otros"
        ),
        "Herramientas reutilizables" to listOf(
            "Pinceles",
            "Esponjas",
            "Otras"
        ),
        "Agujas y accesorios de injerto" to listOf(
            "Agujas",
            "Soportes y mangos",
            "Otros accesorios de injerto"
        ),
        "Ropa y accesorios de bebé" to listOf(
            "Ropa",
            "Gorros",
            "Calcetines y zapatos",
            "Mantas",
            "Chupetes",
            "Diademas y accesorios",
            "Otros"
        ),
        "Embalaje y envíos" to listOf(
            "Cajas",
            "Bolsas",
            "Papel protector",
            "Relleno protector",
            "Etiquetas",
            "Tarjetas y folletos",
            "Material decorativo",
            "Otros materiales de envío"
        ),
        "Otros materiales" to listOf(
            "Otros materiales generales"
        )
    )

    val categoriesWithSubcategories = masterCatalog

    val needleSizes = listOf(
        "36G, 3 barbas",
        "40G, 3 barbas",
        "42G, 3 barbas",
        "42G, 1-2 barbas",
        "42G, 1 barba",
        "43G, 6 muescas",
        "43G, 1 barba",
        "46G, 6 barbas"
    )

    val validUnitsPerFamily: Map<String, List<String>> = mapOf(
        "Kits y esculturas" to listOf("unidades"),
        "Ojos" to listOf("pares", "unidades"),
        "Pelo y cabello" to listOf("g", "unidades"),
        "Imprimaciones y preparación" to listOf("ml", "unidades", "botes"),
        "Pinturas y pigmentos" to listOf("ml", "g", "unidades", "botes"),
        "Barnices y acabados" to listOf("ml", "unidades", "botes"),
        "Rellenos y materiales internos" to listOf("g", "kg", "unidades"),
        "Adhesivos y consumibles auxiliares" to listOf("ml", "unidades", "tubos"),
        "Herramientas reutilizables" to listOf("unidades"),
        "Agujas y accesorios de injerto" to listOf("unidades", "paquetes"),
        "Ropa y accesorios de bebé" to listOf("unidades", "conjuntos"),
        "Embalaje y envíos" to listOf("unidades", "paquetes", "rollos"),
        "Otros materiales" to listOf("unidades", "g", "ml")
    )

    fun getDefaultUnit(family: String): String {
        return validUnitsPerFamily[family]?.firstOrNull() ?: "unidades"
    }

    fun isUnitDivisible(unit: String): Boolean {
        val u = unit.trim().lowercase()
        return u == "g" || u == "gramos" || u == "kg" || u == "ml" || u == "l" || u == "litros"
    }
}
