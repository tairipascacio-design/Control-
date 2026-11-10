package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.DeliveryOrder
import com.example.data.model.DeliveryRound
import com.example.data.model.DeliveryShift
import com.example.data.model.DestinationArea
import com.example.data.model.Expense
import com.example.data.model.ExpenseCategory
import com.example.data.model.InventoryItem
import com.example.data.model.PaymentMethod
import com.example.data.model.ProductCategory
import com.example.data.model.Purchase
import com.example.data.model.Sale
import com.example.data.model.SalesChannel
import com.example.data.model.WasteReason
import com.example.data.model.WasteRecord
import com.example.data.repository.DefaultAiAssistantRepository
import com.example.data.repository.FinancialSummary
import com.example.data.repository.ParsedTicketResult
import com.example.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RestaurantViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = RestaurantRepository(database)
    val aiRepository = DefaultAiAssistantRepository(application)

    val purchases = repository.purchases.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val dailyPurchases = repository.dailyPurchases.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val inventory = repository.inventory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val sales = repository.sales.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val deliveryShifts = repository.deliveryShifts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val deliveryOrders = repository.deliveryOrders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val expenses = repository.expenses.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val wasteRecords = repository.wasteRecords.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val isSpeaking = aiRepository.isSpeaking

    private val _ticketParsingState = MutableStateFlow<ParsedTicketResult?>(null)
    val ticketParsingState: StateFlow<ParsedTicketResult?> = _ticketParsingState.asStateFlow()

    private val _isProcessingAi = MutableStateFlow(false)
    val isProcessingAi: StateFlow<Boolean> = _isProcessingAi.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    val summary: StateFlow<FinancialSummary> = combine(
        combine(sales, purchases, expenses) { s, p, e -> Triple(s, p, e) },
        combine(wasteRecords, inventory, deliveryShifts) { w, i, d -> Triple(w, i, d) }
    ) { (s, p, e), (w, i, d) ->
        repository.calculateSummary(s, p, e, w, i, d)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinancialSummary()
    )

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun addPurchase(
        supplier: String,
        category: ProductCategory,
        productName: String,
        quantity: Double,
        unit: String,
        unitPrice: Double,
        destination: DestinationArea,
        isDailyPurchase: Boolean,
        ticketPhotoUri: String? = null,
        ticketTextNotes: String? = null,
        workerName: String = "Trabajador de Compras"
    ) {
        viewModelScope.launch {
            val total = quantity * unitPrice
            val purchase = Purchase(
                dateMillis = System.currentTimeMillis(),
                supplier = supplier,
                category = category,
                productName = productName,
                quantity = quantity,
                unit = unit,
                unitPrice = unitPrice,
                totalAmount = total,
                destination = destination,
                isDailyPurchase = isDailyPurchase,
                ticketPhotoUri = ticketPhotoUri,
                ticketTextNotes = ticketTextNotes,
                workerName = workerName
            )
            repository.registerPurchase(purchase)
            _userMessage.value = "Compra registrada por $workerName: $productName"
        }
    }

    fun deletePurchase(purchase: Purchase) {
        viewModelScope.launch {
            repository.deletePurchase(purchase)
            _userMessage.value = "Compra eliminada: ${purchase.productName}"
        }
    }

    fun addSale(
        channel: SalesChannel,
        paymentMethod: PaymentMethod,
        productName: String,
        quantity: Double,
        unitPrice: Double,
        deliveryRoundIndex: Int = 1,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val total = quantity * unitPrice
            val sale = Sale(
                dateMillis = System.currentTimeMillis(),
                channel = channel,
                paymentMethod = paymentMethod,
                productName = productName,
                quantity = quantity,
                unitPrice = unitPrice,
                totalAmount = total,
                deliveryRoundIndex = deliveryRoundIndex,
                notes = notes
            )
            repository.registerSale(sale)
            _userMessage.value = "Venta cobrada: $${"%.2f".format(total)} en ${paymentMethod.displayName} (${channel.displayName})"
        }
    }

    fun deleteSale(sale: Sale) {
        viewModelScope.launch {
            repository.deleteSale(sale)
            _userMessage.value = "Venta eliminada"
        }
    }

    fun addDeliveryShift(
        round: DeliveryRound,
        driverName: String,
        itemsLoaded: Int,
        itemsDelivered: Int,
        itemsReturned: Int,
        cashCollected: Double,
        depositsCollected: Double,
        notes: String,
        isClosed: Boolean
    ) {
        val totalOrders = if (itemsLoaded > 0) (itemsLoaded / 2).coerceAtLeast(1) else 0
        val summaryStr = "$totalOrders pedidos cargados ($itemsLoaded medios litros)"
        addDeliveryShiftWithOrders(
            round = round,
            driverName = driverName,
            totalOrdersCount = totalOrders,
            totalGuisadosCount = itemsLoaded,
            ordersSummary = summaryStr,
            itemsLoaded = itemsLoaded,
            itemsDelivered = itemsDelivered,
            itemsReturned = itemsReturned,
            cashCollected = cashCollected,
            depositsCollected = depositsCollected,
            notes = notes,
            isClosed = isClosed,
            orders = emptyList()
        )
    }

    fun addDeliveryShiftWithOrders(
        round: DeliveryRound,
        driverName: String,
        totalOrdersCount: Int,
        totalGuisadosCount: Int,
        ordersSummary: String,
        itemsLoaded: Int,
        itemsDelivered: Int,
        itemsReturned: Int,
        cashCollected: Double,
        depositsCollected: Double,
        notes: String,
        isClosed: Boolean,
        orders: List<DeliveryOrder>
    ) {
        viewModelScope.launch {
            val shift = DeliveryShift(
                dateMillis = System.currentTimeMillis(),
                round = round,
                driverName = driverName,
                totalOrdersCount = totalOrdersCount,
                totalGuisadosCount = totalGuisadosCount,
                ordersSummary = ordersSummary,
                itemsLoaded = itemsLoaded,
                itemsDelivered = itemsDelivered,
                itemsReturned = itemsReturned,
                cashCollected = cashCollected,
                depositsCollected = depositsCollected,
                notes = notes,
                isClosed = isClosed
            )
            val shiftId = repository.registerDeliveryShiftWithOrders(shift, orders)

            if (isClosed && (cashCollected > 0 || depositsCollected > 0)) {
                if (cashCollected > 0) {
                    repository.registerSale(
                        Sale(
                            dateMillis = System.currentTimeMillis(),
                            channel = SalesChannel.MEDIOS_LITROS,
                            paymentMethod = PaymentMethod.EFECTIVO,
                            productName = "Medios Litros - ${round.displayName}",
                            quantity = (itemsDelivered / 2.0).coerceAtLeast(1.0),
                            unitPrice = cashCollected / (itemsDelivered / 2.0).coerceAtLeast(1.0),
                            totalAmount = cashCollected,
                            deliveryRoundIndex = round.roundIndex,
                            notes = "Liquidación Efectivo de ${round.displayName} ($totalOrdersCount pedidos)"
                        )
                    )
                }
                if (depositsCollected > 0) {
                    repository.registerSale(
                        Sale(
                            dateMillis = System.currentTimeMillis(),
                            channel = SalesChannel.MEDIOS_LITROS,
                            paymentMethod = PaymentMethod.DEPOSITO,
                            productName = "Medios Litros - ${round.displayName} (Depósito)",
                            quantity = 1.0,
                            unitPrice = depositsCollected,
                            totalAmount = depositsCollected,
                            deliveryRoundIndex = round.roundIndex,
                            notes = "Liquidación Transferencia/Depósito de ${round.displayName}"
                        )
                    )
                }
            }

            _userMessage.value = "${round.displayName} registrada ($totalOrdersCount pedidos, $itemsLoaded medios litros)"
        }
    }

    fun toggleDeliveryOrderStatus(order: DeliveryOrder, isDelivered: Boolean, isReturned: Boolean) {
        viewModelScope.launch {
            repository.updateDeliveryOrder(order.copy(isDelivered = isDelivered, isReturned = isReturned))
            _userMessage.value = "Estado del ${order.customerOrOrderTag} actualizado"
        }
    }

    fun updateDeliveryShift(shift: DeliveryShift) {
        viewModelScope.launch {
            repository.updateDeliveryShift(shift)
            _userMessage.value = "Vuelta de reparto actualizada"
        }
    }

    fun deleteDeliveryShift(shift: DeliveryShift) {
        viewModelScope.launch {
            repository.deleteDeliveryShift(shift)
            _userMessage.value = "Vuelta de reparto eliminada"
        }
    }

    fun addExpense(
        category: ExpenseCategory,
        description: String,
        amount: Double,
        paymentMethod: PaymentMethod = PaymentMethod.EFECTIVO,
        receiptPhotoUri: String? = null
    ) {
        viewModelScope.launch {
            val expense = Expense(
                dateMillis = System.currentTimeMillis(),
                category = category,
                description = description,
                amount = amount,
                paymentMethod = paymentMethod,
                receiptPhotoUri = receiptPhotoUri
            )
            repository.registerExpense(expense)
            _userMessage.value = "Egreso registrado: ${category.displayName} ($${"%.2f".format(amount)})"
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            _userMessage.value = "Egreso eliminado"
        }
    }

    fun addWaste(
        productName: String,
        category: ProductCategory,
        quantity: Double,
        unit: String,
        estimatedCost: Double,
        reason: WasteReason,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val waste = WasteRecord(
                dateMillis = System.currentTimeMillis(),
                productName = productName,
                category = category,
                quantity = quantity,
                unit = unit,
                estimatedCost = estimatedCost,
                reason = reason,
                notes = notes
            )
            repository.registerWaste(waste)
            _userMessage.value = "Merma descontada de inventario: $quantity $unit de $productName (-$${"%.2f".format(estimatedCost)})"
        }
    }

    fun deleteWaste(waste: WasteRecord) {
        viewModelScope.launch {
            repository.deleteWaste(waste)
            _userMessage.value = "Registro de merma eliminado"
        }
    }

    fun updateStock(itemId: Long, newStock: Double, newCost: Double? = null) {
        viewModelScope.launch {
            repository.updateInventoryStock(itemId, newStock, newCost)
            _userMessage.value = "Stock de inventario actualizado"
        }
    }

    fun recordKitchenUsage(productName: String, quantity: Double) {
        viewModelScope.launch {
            repository.recordKitchenConsumption(productName, quantity)
            _userMessage.value = "Consumo en cocina registrado: -$quantity de $productName"
        }
    }

    fun analyzeTicket(uri: Uri?) {
        viewModelScope.launch {
            _isProcessingAi.value = true
            try {
                val parsed = aiRepository.analyzeTicketPhoto(uri)
                _ticketParsingState.value = parsed
            } finally {
                _isProcessingAi.value = false
            }
        }
    }

    fun clearTicketParsingState() {
        _ticketParsingState.value = null
    }

    fun speakSummaryAloud() {
        val currSummary = summary.value
        val speechText = """
            Resumen del restaurante Control Total:
            Ventas totales: ${currSummary.totalSales.toInt()} pesos.
            Cobrado en efectivo: ${currSummary.totalCash.toInt()} pesos.
            Cobrado en tarjetas salón: ${currSummary.totalCards.toInt()} pesos.
            Cobrado en transferencias y depósitos: ${currSummary.totalDeposits.toInt()} pesos.
            Ventas de medios litros en reparto: ${currSummary.salesMediosLitros.toInt()} pesos.
            Ventas en salón y caja registradora: ${currSummary.salesRestaurante.toInt()} pesos.
            Compras de insumos: ${currSummary.totalPurchases.toInt()} pesos.
            Egresos operativos: ${currSummary.totalExpenses.toInt()} pesos.
            Mercancía invertida en almacén: ${currSummary.totalInventoryValue.toInt()} pesos.
            Costo de mermas: ${currSummary.totalWasteCost.toInt()} pesos.
            Ganancia neta total: ${currSummary.netProfit.toInt()} pesos.
            Diagnóstico: ${currSummary.profitabilityStatus}.
        """.trimIndent()

        aiRepository.speakSummary(speechText)
    }

    fun stopSpeaking() {
        aiRepository.stopSpeaking()
    }

    override fun onCleared() {
        super.onCleared()
        aiRepository.shutdown()
    }
}
