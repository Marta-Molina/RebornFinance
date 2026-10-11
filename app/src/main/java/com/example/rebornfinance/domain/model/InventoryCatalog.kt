package com.example.rebornfinance.domain.model

object InventoryCatalog {
    val masterCatalog = mapOf(
        "Kits y esculturas" to listOf(
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
            "Ojos de silicona",
            "Ojos de otros materiales",
            "Otros artículos y componentes para ojos"
        ),
        "Pelo y cabello" to listOf(
            "Mohair",
            "Pelo humano",
            "Pelo sintético",
            "Alpaca",
            "Otros tipos de pelo",
            "Pelucas y piezas de pelo preparadas"
        ),
        "Imprimaciones y preparación de superficies" to listOf(
            "Imprimación para vinilo",
            "Imprimación para silicona",
            "Preparadores de superficie",
            "Selladores de preparación",
            "Otros productos de preparación"
        ),
        "Pinturas y pigmentos" to listOf(
            "Pinturas acrílicas de secado al aire (Air Dry)",
            "Pinturas específicas para vinilo",
            "Pinturas específicas para silicona",
            "Pinturas termosellables",
            "Pigmentos en polvo",
            "Pinturas para detalles",
            "Otros tipos de pintura y pigmento"
        ),
        "Barnices y acabados" to listOf(
            "Barnices mate",
            "Barnices satinados",
            "Barnices brillantes",
            "Selladores finales",
            "Otros barnices y acabados"
        ),
        "Rellenos y pesos" to listOf(
            "Guata",
            "Fibra de relleno",
            "Cuentas de vidrio",
            "Granulado de vidrio",
            "Microesferas",
            "Discos de acero",
            "Pellets de plástico",
            "Otros rellenos y materiales de peso"
        ),
        "Adhesivos y consumibles auxiliares" to listOf(
            "Pegamentos y adhesivos generales",
            "Adhesivos para pelo",
            "Adhesivos para imanes",
            "Selladores auxiliares",
            "Disolventes",
            "Limpiadores",
            "Cintas",
            "Imanes y componentes auxiliares",
            "Otros consumibles auxiliares"
        ),
        "Herramientas reutilizables" to listOf(
            "Pinceles",
            "Esponjas reutilizables",
            "Herramientas de pintura",
            "Herramientas de montaje",
            "Alicates y pinzas",
            "Utensilios de medición",
            "Otras herramientas reutilizables"
        ),
        "Agujas y accesorios de injerto" to listOf(
            "Agujas de injerto",
            "Soportes y mangos para agujas",
            "Otros accesorios de injerto"
        ),
        "Ropa y complementos del bebé" to listOf(
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
            "Material acolchado y protection",
            "Etiquetas",
            "Papelería de envío: tarjetas y flyers",
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
}
