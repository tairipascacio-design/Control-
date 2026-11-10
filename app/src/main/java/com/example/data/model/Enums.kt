package com.example.data.model

enum class ProductCategory(val displayName: String) {
    CARNES("Carnes"),
    LACTEOS("Lácteos"),
    ABARROTES("Abarrotes"),
    FRUTAS_VERDURAS("Frutas y Verduras"),
    LIMPIEZA("Limpieza"),
    SECOS("Secos / Abarrote Seco"),
    CONSUMIBLES("Consumibles / Envases"),
    OTROS("Otros Insumos")
}

enum class DestinationArea(val displayName: String) {
    MEDIOS_LITROS("Medios Litros"),
    RESTAURANTE("Restaurante / Salón"),
    GENERAL("Ambos / General")
}

enum class SalesChannel(val displayName: String) {
    MEDIOS_LITROS("Reparto (Medios Litros)"),
    RESTAURANTE("Salón (Restaurante)")
}

enum class DeliveryRound(val displayName: String, val roundIndex: Int) {
    VUELTA_1("Vuelta 1 (Mañana)", 1),
    VUELTA_2("Vuelta 2 (Mediodía)", 2),
    VUELTA_3("Vuelta 3 (Tarde)", 3),
    VUELTA_DOMINGO("Vuelta Única (Domingo)", 4)
}

enum class PaymentMethod(val displayName: String) {
    EFECTIVO("Efectivo"),
    TARJETA("Tarjeta TPV"),
    DEPOSITO("Depósito / Transferencia")
}

enum class ExpenseCategory(val displayName: String) {
    RENTA("Renta del Local"),
    LUZ("Energía Eléctrica (Luz)"),
    GAS("Gas LP Cocina"),
    SUELDOS("Sueldos y Propinas"),
    MANTENIMIENTO("Mantenimiento y Reparaciones"),
    OTRO("Otro Gasto Operativo")
}

enum class WasteReason(val displayName: String) {
    CADUCIDAD("Caducidad / Descomposición"),
    SOBRANTE_NO_VENDIDO("Sobrante de Reparto / No Vendido"),
    ERROR_COCINA("Error en Preparación"),
    DANO_EMPAQUE("Empaque Dañado / Merma")
}
