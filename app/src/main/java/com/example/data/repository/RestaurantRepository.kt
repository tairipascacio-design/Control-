package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.DeliveryOrder
import com.example.data.model.DeliveryShift
import com.example.data.model.Expense
import com.example.data.model.InventoryItem
import com.example.data.model.PaymentMethod
import com.example.data.model.Purchase
import com.example.data.model.Sale
import com.example.data.model.SalesChannel
import com.example.data.model.WasteRecord
import kotlinx.coroutines.flow.Flow

data class FinancialSummary(
    val totalSales: Double = 0.0,
    val salesMediosLitros: Double = 0.0,
    val salesRestaurante: Double = 0.0,
    val totalCash: Double = 0.0,
    val totalCards: Double = 0.0,
    val totalDeposits: Double = 0.0,
    val totalPurchases: Double = 0.0,
    val dailyPurchases: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val totalWasteCost: Double = 0.0,
    val totalInventoryValue: Double = 0.0,
    val netProfit: Double = 0.0,
    val operatingMarginPct: Double = 0.0,
    val isProfitable: Boolean = true,
    val profitabilityStatus: String = "Excelente Rentabilidad",
    val statusRecommendation: String = ""
)

class RestaurantRepository(private val database: AppDatabase) {
    private val purchaseDao = database.purchaseDao()
    private val inventoryDao = database.inventoryDao()
    private val saleDao = database.saleDao()
    private val deliveryShiftDao = database.deliveryShiftDao()
    private val deliveryOrderDao = database.deliveryOrderDao()
    private val expenseDao = database.expenseDao()
    private val wasteDao = database.wasteDao()

    val purchases: Flow<List<Purchase>> = purchaseDao.getAllPurchases()
    val dailyPurchases: Flow<List<Purchase>> = purchaseDao.getDailyPurchases()
    val inventory: Flow<List<InventoryItem>> = inventoryDao.getAllInventory()
    val sales: Flow<List<Sale>> = saleDao.getAllSales()
    val deliveryShifts: Flow<List<DeliveryShift>> = deliveryShiftDao.getAllDeliveryShifts()
    val deliveryOrders: Flow<List<DeliveryOrder>> = deliveryOrderDao.getAllDeliveryOrders()
    val expenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
    val wasteRecords: Flow<List<WasteRecord>> = wasteDao.getAllWaste()

    suspend fun registerPurchase(purchase: Purchase): Long {
        val id = purchaseDao.insertPurchase(purchase)
        val trimmedName = purchase.productName.trim()
        val existingItem = inventoryDao.findByName(trimmedName)
        if (existingItem != null) {
            val newStock = (existingItem.currentStock + purchase.quantity).coerceAtLeast(0.0)
            val updatedItem = existingItem.copy(
                currentStock = newStock,
                unitCost = purchase.unitPrice,
                lastUpdatedMillis = System.currentTimeMillis(),
                category = purchase.category,
                unit = purchase.unit
            )
            inventoryDao.updateItem(updatedItem)
        } else {
            val newItem = InventoryItem(
                productName = trimmedName,
                category = purchase.category,
                currentStock = purchase.quantity,
                unit = purchase.unit,
                unitCost = purchase.unitPrice,
                destination = purchase.destination,
                lastUpdatedMillis = System.currentTimeMillis()
            )
            inventoryDao.insertOrUpdate(newItem)
        }
        return id
    }

    suspend fun deletePurchase(purchase: Purchase) {
        purchaseDao.deletePurchase(purchase)
    }

    suspend fun updateInventoryStock(itemId: Long, newStock: Double, newCost: Double? = null) {
        val existing = inventoryDao.findById(itemId) ?: return
        val updated = existing.copy(
            currentStock = newStock.coerceAtLeast(0.0),
            unitCost = newCost ?: existing.unitCost,
            lastUpdatedMillis = System.currentTimeMillis()
        )
        inventoryDao.updateItem(updated)
    }

    suspend fun registerWaste(waste: WasteRecord): Long {
        val id = wasteDao.insertWaste(waste)
        val existing = inventoryDao.findByName(waste.productName.trim())
        if (existing != null) {
            val newStock = (existing.currentStock - waste.quantity).coerceAtLeast(0.0)
            inventoryDao.updateItem(existing.copy(currentStock = newStock, lastUpdatedMillis = System.currentTimeMillis()))
        }
        return id
    }

    suspend fun deleteWaste(waste: WasteRecord) {
        wasteDao.deleteWaste(waste)
    }

    suspend fun registerSale(sale: Sale): Long {
        return saleDao.insertSale(sale)
    }

    suspend fun deleteSale(sale: Sale) {
        saleDao.deleteSale(sale)
    }

    suspend fun registerDeliveryShift(shift: DeliveryShift): Long {
        return deliveryShiftDao.insertShift(shift)
    }

    suspend fun registerDeliveryShiftWithOrders(
        shift: DeliveryShift,
        orders: List<DeliveryOrder>
    ): Long {
        val shiftId = deliveryShiftDao.insertShift(shift)
        if (orders.isNotEmpty()) {
            val ordersWithShift = orders.map { it.copy(shiftId = shiftId) }
            deliveryOrderDao.insertOrders(ordersWithShift)
        }
        return shiftId
    }

    suspend fun updateDeliveryShift(shift: DeliveryShift) {
        deliveryShiftDao.updateShift(shift)
    }

    suspend fun deleteDeliveryShift(shift: DeliveryShift) {
        deliveryShiftDao.deleteShift(shift)
    }

    suspend fun updateDeliveryOrder(order: DeliveryOrder) {
        deliveryOrderDao.updateOrder(order)
    }

    suspend fun deleteDeliveryOrder(order: DeliveryOrder) {
        deliveryOrderDao.deleteOrder(order)
    }

    suspend fun registerExpense(expense: Expense): Long {
        return expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun recordKitchenConsumption(productName: String, quantityUsed: Double) {
        val existing = inventoryDao.findByName(productName.trim()) ?: return
        val newStock = (existing.currentStock - quantityUsed).coerceAtLeast(0.0)
        inventoryDao.updateItem(existing.copy(currentStock = newStock, lastUpdatedMillis = System.currentTimeMillis()))
    }

    fun calculateSummary(
        salesList: List<Sale>,
        purchasesList: List<Purchase>,
        expensesList: List<Expense>,
        wasteList: List<WasteRecord>,
        inventoryList: List<InventoryItem>,
        deliveryShiftsList: List<DeliveryShift>
    ): FinancialSummary {
        val totalSales = salesList.sumOf { it.totalAmount }
        val salesMediosLitros = salesList.filter { it.channel == SalesChannel.MEDIOS_LITROS }.sumOf { it.totalAmount }
        val salesRestaurante = salesList.filter { it.channel == SalesChannel.RESTAURANTE }.sumOf { it.totalAmount }

        val totalCash = salesList.filter { it.paymentMethod == PaymentMethod.EFECTIVO }.sumOf { it.totalAmount }
        val totalCards = salesList.filter { it.paymentMethod == PaymentMethod.TARJETA }.sumOf { it.totalAmount }
        val totalDeposits = salesList.filter { it.paymentMethod == PaymentMethod.DEPOSITO }.sumOf { it.totalAmount }

        val totalPurchases = purchasesList.sumOf { it.totalAmount }
        val dailyPurchases = purchasesList.filter { it.isDailyPurchase }.sumOf { it.totalAmount }
        val totalExpenses = expensesList.sumOf { it.amount }
        val totalWasteCost = wasteList.sumOf { it.estimatedCost }
        val totalInventoryValue = inventoryList.sumOf { it.totalValue }

        val netProfit = (totalSales - totalPurchases - totalExpenses + totalInventoryValue - totalWasteCost)
        val marginPct = if (totalSales > 0) ((netProfit / totalSales) * 100.0) else 0.0

        val (status, recommendation) = when {
            netProfit > 2500 && marginPct >= 30.0 -> {
                "Muy Costeable y Rentable" to "¡Excelente margen! Las 3 vueltas de reparto y las mesas de salón están cubriendo con holgura los insumos y la inversión en inventario."
            }
            netProfit >= 0 -> {
                "Negocio Estable / Costeable" to "El restaurante es costeable. Vigila mermas y compras diarias para elevar las ganancias semanales."
            }
            else -> {
                "Margen Ajustado / Revisar Costos" to "Atención: Los costos superaron las ventas. Revisa precios por medio litro y controla sobrantes de vueltas de reparto."
            }
        }

        return FinancialSummary(
            totalSales = totalSales,
            salesMediosLitros = salesMediosLitros,
            salesRestaurante = salesRestaurante,
            totalCash = totalCash,
            totalCards = totalCards,
            totalDeposits = totalDeposits,
            totalPurchases = totalPurchases,
            dailyPurchases = dailyPurchases,
            totalExpenses = totalExpenses,
            totalWasteCost = totalWasteCost,
            totalInventoryValue = totalInventoryValue,
            netProfit = netProfit,
            operatingMarginPct = marginPct,
            isProfitable = netProfit >= 0,
            profitabilityStatus = status,
            statusRecommendation = recommendation
        )
    }
}
