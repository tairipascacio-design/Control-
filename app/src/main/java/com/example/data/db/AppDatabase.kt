package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Purchase::class,
        InventoryItem::class,
        Sale::class,
        DeliveryShift::class,
        DeliveryOrder::class,
        Expense::class,
        WasteRecord::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun purchaseDao(): PurchaseDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun saleDao(): SaleDao
    abstract fun deliveryShiftDao(): DeliveryShiftDao
    abstract fun deliveryOrderDao(): DeliveryOrderDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun wasteDao(): WasteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "restaurant_control_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val purchaseDao = database.purchaseDao()
            val inventoryDao = database.inventoryDao()
            val saleDao = database.saleDao()
            val deliveryDao = database.deliveryShiftDao()
            val deliveryOrderDao = database.deliveryOrderDao()
            val expenseDao = database.expenseDao()
            val wasteDao = database.wasteDao()

            val now = System.currentTimeMillis()
            val oneDay = 24 * 60 * 60 * 1000L

            val samplePurchases = listOf(
                Purchase(
                    dateMillis = now - (oneDay * 2),
                    supplier = "Carnicería San Juan",
                    category = ProductCategory.CARNES,
                    productName = "Carne de Res molida",
                    quantity = 15.0,
                    unit = "kg",
                    unitPrice = 140.0,
                    totalAmount = 2100.0,
                    destination = DestinationArea.MEDIOS_LITROS,
                    isDailyPurchase = false,
                    workerName = "Juan (Trabajador de Compras)",
                    ticketTextNotes = "Ticket #A-8942 Carnicería San Juan. 15 kg pulpa/molida para medios litros de guisados."
                ),
                Purchase(
                    dateMillis = now - (oneDay * 1),
                    supplier = "Central de Abastos",
                    category = ProductCategory.FRUTAS_VERDURAS,
                    productName = "Tomate Huaje y Cebolla",
                    quantity = 25.0,
                    unit = "kg",
                    unitPrice = 24.0,
                    totalAmount = 600.0,
                    destination = DestinationArea.GENERAL,
                    isDailyPurchase = false,
                    workerName = "Juan (Trabajador de Compras)",
                    ticketTextNotes = "Factura B-102. Jitomate bola y cebolla blanca para salsas y guisos."
                ),
                Purchase(
                    dateMillis = now,
                    supplier = "Panadería El Rosario",
                    category = ProductCategory.SECOS,
                    productName = "Bolillos y Pan del día",
                    quantity = 50.0,
                    unit = "pza",
                    unitPrice = 5.0,
                    totalAmount = 250.0,
                    destination = DestinationArea.RESTAURANTE,
                    isDailyPurchase = true,
                    workerName = "Juan (Trabajador de Compras)",
                    ticketTextNotes = "Compra diaria de pan fresco matutino."
                )
            )
            samplePurchases.forEach { purchaseDao.insertPurchase(it) }

            val sampleInventory = listOf(
                InventoryItem(
                    productName = "Carne de Res molida",
                    category = ProductCategory.CARNES,
                    currentStock = 8.5,
                    unit = "kg",
                    unitCost = 140.0,
                    destination = DestinationArea.MEDIOS_LITROS,
                    lastUpdatedMillis = now
                ),
                InventoryItem(
                    productName = "Tomate Huaje y Cebolla",
                    category = ProductCategory.FRUTAS_VERDURAS,
                    currentStock = 14.0,
                    unit = "kg",
                    unitCost = 24.0,
                    destination = DestinationArea.GENERAL,
                    lastUpdatedMillis = now
                ),
                InventoryItem(
                    productName = "Envases de Medio Litro con Tapa",
                    category = ProductCategory.CONSUMIBLES,
                    currentStock = 250.0,
                    unit = "pza",
                    unitCost = 2.80,
                    destination = DestinationArea.MEDIOS_LITROS,
                    lastUpdatedMillis = now
                )
            )
            sampleInventory.forEach { inventoryDao.insertOrUpdate(it) }

            // Turno de Reparto con desglose por pedido (10 pedidos: 5 de 2, 3 de 1, 2 de 5)
            val shift1 = DeliveryShift(
                dateMillis = now - 4 * 60 * 60 * 1000L,
                round = DeliveryRound.VUELTA_1,
                driverName = "Mario (Repartidor)",
                totalOrdersCount = 10,
                totalGuisadosCount = 23,
                ordersSummary = "5 pedidos de 2 guisados • 3 pedidos de 1 guisado • 2 pedidos de 5 guisados",
                itemsLoaded = 23,
                itemsDelivered = 23,
                itemsReturned = 0,
                cashCollected = 975.0,
                depositsCollected = 750.0,
                notes = "Vuelta 1 mañana: 10 pedidos entregados sin devoluciones",
                isClosed = true
            )
            val shift1Id = deliveryDao.insertShift(shift1)

            val shift1Orders = listOf(
                DeliveryOrder(shiftId = shift1Id, customerOrOrderTag = "Pedido #1 (Taller Hermanos)", guisadosCount = 2, guisadosDetail = "1 Asado, 1 Tinga", pricePerGuisado = 75.0, totalAmount = 150.0, paymentMethod = PaymentMethod.EFECTIVO, isDelivered = true),
                DeliveryOrder(shiftId = shift1Id, customerOrOrderTag = "Pedido #2 (Oficinas Gómez)", guisadosCount = 2, guisadosDetail = "2 Asado con Arroz", pricePerGuisado = 75.0, totalAmount = 150.0, paymentMethod = PaymentMethod.DEPOSITO, isDelivered = true),
                DeliveryOrder(shiftId = shift1Id, customerOrOrderTag = "Pedido #3 (Consultorio)", guisadosCount = 2, guisadosDetail = "1 Pozole, 1 Bistec", pricePerGuisado = 75.0, totalAmount = 150.0, paymentMethod = PaymentMethod.EFECTIVO, isDelivered = true),
                DeliveryOrder(shiftId = shift1Id, customerOrOrderTag = "Pedido #4 (Refaccionaria)", guisadosCount = 2, guisadosDetail = "2 Chicharrón verde", pricePerGuisado = 75.0, totalAmount = 150.0, paymentMethod = PaymentMethod.EFECTIVO, isDelivered = true),
                DeliveryOrder(shiftId = shift1Id, customerOrOrderTag = "Pedido #5 (Ferretería)", guisadosCount = 2, guisadosDetail = "1 Tinga, 1 Asado", pricePerGuisado = 75.0, totalAmount = 150.0, paymentMethod = PaymentMethod.DEPOSITO, isDelivered = true),
                DeliveryOrder(shiftId = shift1Id, customerOrOrderTag = "Pedido #6 (Farmacia)", guisadosCount = 1, guisadosDetail = "1 Asado", pricePerGuisado = 75.0, totalAmount = 75.0, paymentMethod = PaymentMethod.EFECTIVO, isDelivered = true),
                DeliveryOrder(shiftId = shift1Id, customerOrOrderTag = "Pedido #7 (Estética)", guisadosCount = 1, guisadosDetail = "1 Tinga", pricePerGuisado = 75.0, totalAmount = 75.0, paymentMethod = PaymentMethod.EFECTIVO, isDelivered = true),
                DeliveryOrder(shiftId = shift1Id, customerOrOrderTag = "Pedido #8 (Zapatería)", guisadosCount = 1, guisadosDetail = "1 Bistec", pricePerGuisado = 75.0, totalAmount = 75.0, paymentMethod = PaymentMethod.EFECTIVO, isDelivered = true),
                DeliveryOrder(shiftId = shift1Id, customerOrOrderTag = "Pedido #9 (Constructora)", guisadosCount = 5, guisadosDetail = "3 Asado, 2 Tinga", pricePerGuisado = 75.0, totalAmount = 375.0, paymentMethod = PaymentMethod.DEPOSITO, isDelivered = true),
                DeliveryOrder(shiftId = shift1Id, customerOrOrderTag = "Pedido #10 (Bodega)", guisadosCount = 5, guisadosDetail = "2 Chicharrón, 3 Pozole", pricePerGuisado = 75.0, totalAmount = 375.0, paymentMethod = PaymentMethod.EFECTIVO, isDelivered = true)
            )
            deliveryOrderDao.insertOrders(shift1Orders)

            val shift2 = DeliveryShift(
                dateMillis = now - 1 * 60 * 60 * 1000L,
                round = DeliveryRound.VUELTA_2,
                driverName = "Mario (Repartidor)",
                totalOrdersCount = 12,
                totalGuisadosCount = 28,
                ordersSummary = "6 pedidos de 2 guisados • 4 pedidos de 1 guisado • 2 pedidos de 6 guisados",
                itemsLoaded = 30,
                itemsDelivered = 28,
                itemsReturned = 2,
                cashCollected = 1400.0,
                depositsCollected = 700.0,
                notes = "Vuelta 2 mediodía pico: alta demanda",
                isClosed = true
            )
            deliveryDao.insertShift(shift2)

            val shift3 = DeliveryShift(
                dateMillis = now,
                round = DeliveryRound.VUELTA_3,
                driverName = "Mario (Repartidor)",
                totalOrdersCount = 6,
                totalGuisadosCount = 15,
                ordersSummary = "3 pedidos de 3 guisados • 3 pedidos de 2 guisados",
                itemsLoaded = 15,
                itemsDelivered = 0,
                itemsReturned = 0,
                cashCollected = 0.0,
                depositsCollected = 0.0,
                notes = "Vuelta 3 tarde en ruta...",
                isClosed = false
            )
            deliveryDao.insertShift(shift3)

            val sampleSales = listOf(
                Sale(
                    dateMillis = now - 4 * 60 * 60 * 1000L,
                    channel = SalesChannel.MEDIOS_LITROS,
                    paymentMethod = PaymentMethod.EFECTIVO,
                    productName = "Medio litro de Asado con Arroz",
                    quantity = 13.0,
                    unitPrice = 75.0,
                    totalAmount = 975.0,
                    deliveryRoundIndex = 1,
                    notes = "Reparto Vuelta 1 - Pago en Efectivo"
                ),
                Sale(
                    dateMillis = now - 4 * 60 * 60 * 1000L,
                    channel = SalesChannel.MEDIOS_LITROS,
                    paymentMethod = PaymentMethod.DEPOSITO,
                    productName = "Medio litro de Tinga de Pollo",
                    quantity = 10.0,
                    unitPrice = 75.0,
                    totalAmount = 750.0,
                    deliveryRoundIndex = 1,
                    notes = "Reparto Vuelta 1 - Transferencia banco confirmada"
                ),
                Sale(
                    dateMillis = now - 2 * 60 * 60 * 1000L,
                    channel = SalesChannel.RESTAURANTE,
                    paymentMethod = PaymentMethod.TARJETA,
                    productName = "Consumo Salón (Mesa 4)",
                    quantity = 4.0,
                    unitPrice = 95.0,
                    totalAmount = 380.0,
                    notes = "Caja Registradora Tablet - Cobro con Terminal Tarjeta"
                ),
                Sale(
                    dateMillis = now - 1 * 60 * 60 * 1000L,
                    channel = SalesChannel.RESTAURANTE,
                    paymentMethod = PaymentMethod.EFECTIVO,
                    productName = "Comida Corrida Salón (Mesa 1 y 2)",
                    quantity = 6.0,
                    unitPrice = 90.0,
                    totalAmount = 540.0,
                    notes = "Caja Registradora Tablet - Efectivo en gaveta"
                )
            )
            sampleSales.forEach { saleDao.insertSale(it) }

            val sampleExpenses = listOf(
                Expense(
                    dateMillis = now - oneDay,
                    category = ExpenseCategory.GAS,
                    description = "Carga de gas LP para estufas",
                    amount = 850.0,
                    paymentMethod = PaymentMethod.EFECTIVO
                )
            )
            sampleExpenses.forEach { expenseDao.insertExpense(it) }

            val sampleWaste = listOf(
                WasteRecord(
                    dateMillis = now - oneDay,
                    productName = "Tomate Huaje",
                    category = ProductCategory.FRUTAS_VERDURAS,
                    quantity = 1.5,
                    unit = "kg",
                    estimatedCost = 36.0,
                    reason = WasteReason.CADUCIDAD,
                    notes = "Maduración excesiva"
                )
            )
            sampleWaste.forEach { wasteDao.insertWaste(it) }
        }
    }
}
