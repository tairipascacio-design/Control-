package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class Purchase(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long = System.currentTimeMillis(),
    val supplier: String = "",
    val category: ProductCategory = ProductCategory.OTROS,
    val productName: String = "",
    val quantity: Double = 1.0,
    val unit: String = "kg",
    val unitPrice: Double = 0.0,
    val totalAmount: Double = 0.0,
    val destination: DestinationArea = DestinationArea.GENERAL,
    val isDailyPurchase: Boolean = false,
    val ticketPhotoUri: String? = null,
    val ticketTextNotes: String? = null,
    val workerName: String = "Trabajador de Compras"
)

@Entity(tableName = "inventory")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productName: String = "",
    val category: ProductCategory = ProductCategory.OTROS,
    val currentStock: Double = 0.0,
    val unit: String = "kg",
    val unitCost: Double = 0.0,
    val destination: DestinationArea = DestinationArea.GENERAL,
    val lastUpdatedMillis: Long = System.currentTimeMillis()
) {
    val totalValue: Double get() = currentStock * unitCost
}

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long = System.currentTimeMillis(),
    val channel: SalesChannel = SalesChannel.MEDIOS_LITROS,
    val paymentMethod: PaymentMethod = PaymentMethod.EFECTIVO,
    val productName: String = "",
    val quantity: Double = 1.0,
    val unitPrice: Double = 0.0,
    val totalAmount: Double = 0.0,
    val deliveryRoundIndex: Int = 1,
    val notes: String = ""
)

@Entity(tableName = "delivery_shifts")
data class DeliveryShift(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long = System.currentTimeMillis(),
    val round: DeliveryRound = DeliveryRound.VUELTA_1,
    val driverName: String = "Mario (Repartidor)",
    val totalOrdersCount: Int = 0,
    val totalGuisadosCount: Int = 0,
    val ordersSummary: String = "",
    val itemsLoaded: Int = 0,
    val itemsDelivered: Int = 0,
    val itemsReturned: Int = 0,
    val cashCollected: Double = 0.0,
    val depositsCollected: Double = 0.0,
    val notes: String = "",
    val isClosed: Boolean = false
) {
    val totalMoneyCollected: Double get() = cashCollected + depositsCollected
}

@Entity(tableName = "delivery_orders")
data class DeliveryOrder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val shiftId: Long = 0L,
    val customerOrOrderTag: String = "",
    val guisadosCount: Int = 1,
    val guisadosDetail: String = "",
    val pricePerGuisado: Double = 75.0,
    val totalAmount: Double = 75.0,
    val paymentMethod: PaymentMethod = PaymentMethod.EFECTIVO,
    val isDelivered: Boolean = true,
    val isReturned: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long = System.currentTimeMillis(),
    val category: ExpenseCategory = ExpenseCategory.OTRO,
    val description: String = "",
    val amount: Double = 0.0,
    val paymentMethod: PaymentMethod = PaymentMethod.EFECTIVO,
    val receiptPhotoUri: String? = null
)

@Entity(tableName = "waste_records")
data class WasteRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long = System.currentTimeMillis(),
    val productName: String = "",
    val category: ProductCategory = ProductCategory.OTROS,
    val quantity: Double = 0.0,
    val unit: String = "kg",
    val estimatedCost: Double = 0.0,
    val reason: WasteReason = WasteReason.CADUCIDAD,
    val notes: String = ""
)
