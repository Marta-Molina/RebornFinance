package com.example.rebornfinance.domain.model

object InventoryCatalog {
    val categoriesWithSubcategories = mapOf(
        "Kits" to listOf(
            "Kits completos",
            "Kits de edición limitada",
            "Kits de edición abierta",
            "Cabezas",
            "Brazos",
            "Piernas",
            "Extremidades completas",
            "Cuerpos de tela",
            "Cuerpos de vinilo",
            "Cuerpos de silicona",
            "Otros cuerpos y piezas"
        ),
        "Ojos" to listOf(
            "Ojos de cristal",
            "Ojos de acrílico",
            "Ojos de silicona u otros materiales disponibles",
            "Ojos durmientes",
            "Otros tipos de ojos"
        ),
        "Pelo y cabello" to listOf(
            "Mohair",
            "Pelo humano",
            "Pelo sintético",
            "Alpaca",
            "Otros tipos de pelo",
            "Pelucas o piezas de pelo preparadas"
        ),
        "Imprimaciones" to listOf(
            "Imprimación para vinilo",
            "Imprimación para silicona",
            "Preparadores de superficie",
            "Selladores de preparación",
            "Otros productos de preparación"
        ),
        "Pinturas y pigmentos" to listOf(
            "Pinturas acrílicas (Air dry)",
            "Pinturas específicas para vinilo",
            "Pinturas específicas para silicona",
            "Pinturas termosellables",
            "Pigmentos en polvo",
            "Pinturas para detalles",
            "Otros tipos de pintura"
        ),
        "Barnices y acabados" to listOf(
            "Barniz mate",
            "Barniz satinado",
            "Barniz brillante",
            "Skin texture",
            "Selladores finales",
            "Otros acabados"
        ),
        "Rellenos y pesos" to listOf(
            "Guata",
            "Fibra de relleno",
            "Cuentas de vidrio",
            "Granulado de vidrio",
            "Microesferas",
            "Material de peso",
            "Otros rellenos"
        ),
        "Adhesivos y consumibles" to listOf(
            "Pegamentos",
            "Adhesivos para pelo",
            "Pegamento para imanes",
            "Selladores",
            "Disolventes",
            "Limpiadores",
            "Cintas",
            "Imanes",
            "Otros consumibles"
        ),
        "Herramientas reutilizables" to listOf(
            "Pinceles",
            "Esponjas reutilizables",
            "Herramientas de pintura",
            "Herramientas de montaje",
            "Herramientas de injerto",
            "Alicates y pinzas",
            "Utensilios de medición",
            "Otras herramientas"
        ),
        "Agujas y accesorios" to listOf(
            "Agujas de injerto",
            "Agujas de diferentes tamaños",
            "Soportes",
            "Accesorios de injerto",
            "Otros accesorios"
        ),
        "Ropa y complementos" to listOf(
            "Ropa",
            "Gorros",
            "Calcetines y zapatos",
            "Mantas",
            "Chupetes",
            "Diademas y accesorios",
            "Otros complementos"
        ),
        "Embalaje y envíos" to listOf(
            "Cajas",
            "Bolsas",
            "Papel de protección",
            "Material acolchado",
            "Etiquetas",
            "Tarjetas y flyers",
            "Material decorativo",
            "Otros materiales de envío"
        ),
        "Otros materiales" to listOf(
            "Materiales diversos",
            "Personalizados"
        )
    )
}
