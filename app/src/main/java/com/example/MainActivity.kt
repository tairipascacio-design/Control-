package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.InventoryItem
import com.example.data.repository.ParsedTicketResult
import com.example.ui.navigation.AppScreen
import com.example.ui.screens.AddPurchaseDialog
import com.example.ui.screens.AudioDictationDialog
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DeliveryScreen
import com.example.ui.screens.DownloadApkDialog
import com.example.ui.screens.ExpensesScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.PurchasesScreen
import com.example.ui.screens.SalonRegisterScreen
import com.example.ui.screens.TicketScanDialog
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.RestaurantViewModel
import com.example.util.configureAppCheck
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: RestaurantViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

                    DisposableEffect(Unit) {
                        val listener = FirebaseAuth.AuthStateListener { auth ->
                            currentUser = auth.currentUser
                        }
                        Firebase.auth.addAuthStateListener(listener)
                        onDispose {
                            Firebase.auth.removeAuthStateListener(listener)
                        }
                    }

                    if (currentUser == null) {
                        AuthScreen(
                            onAuthSuccess = {
                                currentUser = Firebase.auth.currentUser ?: Firebase.auth.currentUser
                            }
                        )
                    } else {
                        RestaurantApp(
                            viewModel = viewModel,
                            onSignOut = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    try {
                                        CredentialManager.create(this@MainActivity)
                                            .clearCredentialState(ClearCredentialStateRequest())
                                    } catch (e: Exception) {
                                        // Ignore
                                    } finally {
                                        Firebase.auth.signOut()
                                        currentUser = null
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantApp(
    viewModel: RestaurantViewModel,
    onSignOut: () -> Unit
) {
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddPurchaseDialog by remember { mutableStateOf(false) }
    var showScanTicketDialog by remember { mutableStateOf(false) }
    var showAudioDictationDialog by remember { mutableStateOf(false) }
    var showDownloadApkDialog by remember { mutableStateOf(false) }
    var prefilledTicketResult by remember { mutableStateOf<ParsedTicketResult?>(null) }
    var prefilledWasteItem by remember { mutableStateOf<InventoryItem?>(null) }

    val purchases by viewModel.purchases.collectAsStateWithLifecycle()
    val inventory by viewModel.inventory.collectAsStateWithLifecycle()
    val sales by viewModel.sales.collectAsStateWithLifecycle()
    val deliveryShifts by viewModel.deliveryShifts.collectAsStateWithLifecycle()
    val deliveryOrders by viewModel.deliveryOrders.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val wasteRecords by viewModel.wasteRecords.collectAsStateWithLifecycle()
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.isSpeaking.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val isProcessingAi by viewModel.isProcessingAi.collectAsStateWithLifecycle()
    val ticketParsingState by viewModel.ticketParsingState.collectAsStateWithLifecycle()

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    if (currentScreen != AppScreen.DASHBOARD) {
        BackHandler {
            currentScreen = AppScreen.DASHBOARD
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Control Total • ${currentScreen.title}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    IconButton(
                        onClick = { showDownloadApkDialog = true },
                        modifier = Modifier.testTag("topbar_btn_download_apk")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Descargar APK para tablets y celulares",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = {
                            if (isSpeaking) viewModel.stopSpeaking() else viewModel.speakSummaryAloud()
                        },
                        modifier = Modifier.testTag("topbar_btn_tts")
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                            contentDescription = "Escuchar resumen en voz alta",
                            tint = if (isSpeaking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = onSignOut,
                        modifier = Modifier.testTag("topbar_btn_signout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                AppScreen.entries.forEach { screen ->
                    NavigationBarItem(
                        selected = currentScreen == screen,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = when (screen) {
                                    AppScreen.DASHBOARD -> "Neto"
                                    AppScreen.PURCHASES -> "Compras"
                                    AppScreen.DELIVERY -> "Reparto"
                                    AppScreen.SALON_REGISTER -> "Caja Salón"
                                    AppScreen.INVENTORY -> "Inventario"
                                    AppScreen.EXPENSES -> "Egresos"
                                },
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.testTag(screen.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> {
                    DashboardScreen(
                        summary = summary,
                        isSpeaking = isSpeaking,
                        onSpeakSummary = { viewModel.speakSummaryAloud() },
                        onStopSpeaking = { viewModel.stopSpeaking() },
                        onNavigateTo = { currentScreen = it },
                        onOpenAddPurchase = {
                            prefilledTicketResult = null
                            showAddPurchaseDialog = true
                        },
                        onOpenAddSale = { currentScreen = AppScreen.SALON_REGISTER }
                    )
                }

                AppScreen.PURCHASES -> {
                    PurchasesScreen(
                        purchases = purchases,
                        onAddPurchaseClick = {
                            prefilledTicketResult = null
                            showAddPurchaseDialog = true
                        },
                        onScanTicketClick = { showScanTicketDialog = true },
                        onVoiceDictateClick = { showAudioDictationDialog = true },
                        onDeletePurchase = { viewModel.deletePurchase(it) }
                    )
                }

                AppScreen.DELIVERY -> {
                    DeliveryScreen(
                        shifts = deliveryShifts,
                        deliveryOrders = deliveryOrders,
                        onAddShiftWithOrders = { round, driver, ordersCount, guisadosCount, summaryStr, loaded, delivered, returned, cash, deposits, notes, isClosed, orders ->
                            viewModel.addDeliveryShiftWithOrders(
                                round, driver, ordersCount, guisadosCount, summaryStr, loaded, delivered, returned, cash, deposits, notes, isClosed, orders
                            )
                        },
                        onToggleOrderStatus = { order, delivered, returned ->
                            viewModel.toggleDeliveryOrderStatus(order, delivered, returned)
                        },
                        onDeleteShift = { viewModel.deleteDeliveryShift(it) }
                    )
                }

                AppScreen.SALON_REGISTER -> {
                    SalonRegisterScreen(
                        sales = sales,
                        onAddSalonSale = { method, dish, qty, price, notes ->
                            viewModel.addSale(
                                channel = com.example.data.model.SalesChannel.RESTAURANTE,
                                paymentMethod = method,
                                productName = dish,
                                quantity = qty,
                                unitPrice = price,
                                notes = notes
                            )
                        },
                        onDeleteSale = { viewModel.deleteSale(it) }
                    )
                }

                AppScreen.INVENTORY -> {
                    InventoryScreen(
                        inventory = inventory,
                        onUpdateStock = { id, stock, cost -> viewModel.updateStock(id, stock, cost) },
                        onRecordConsumption = { prod, qty -> viewModel.recordKitchenUsage(prod, qty) },
                        onNavigateToWasteWithItem = { item ->
                            prefilledWasteItem = item
                            currentScreen = AppScreen.EXPENSES
                        }
                    )
                }

                AppScreen.EXPENSES -> {
                    ExpensesScreen(
                        expenses = expenses,
                        wasteRecords = wasteRecords,
                        inventory = inventory,
                        onAddExpense = { cat, desc, amt, pay, photo ->
                            viewModel.addExpense(cat, desc, amt, pay, photo)
                        },
                        onDeleteExpense = { viewModel.deleteExpense(it) },
                        onAddWaste = { prod, cat, qty, u, cost, reason, notes ->
                            viewModel.addWaste(prod, cat, qty, u, cost, reason, notes)
                        },
                        onDeleteWaste = { viewModel.deleteWaste(it) }
                    )
                }
            }
        }
    }

    if (showAddPurchaseDialog) {
        AddPurchaseDialog(
            initialParsedTicket = prefilledTicketResult,
            onDismiss = {
                showAddPurchaseDialog = false
                prefilledTicketResult = null
            },
            onSave = { supplier, category, prod, qty, unit, unitPrice, dest, isDaily, photo, notes, worker ->
                viewModel.addPurchase(
                    supplier = supplier,
                    category = category,
                    productName = prod,
                    quantity = qty,
                    unit = unit,
                    unitPrice = unitPrice,
                    destination = dest,
                    isDailyPurchase = isDaily,
                    ticketPhotoUri = photo,
                    ticketTextNotes = notes,
                    workerName = worker
                )
                showAddPurchaseDialog = false
                prefilledTicketResult = null
            }
        )
    }

    if (showScanTicketDialog) {
        TicketScanDialog(
            isProcessing = isProcessingAi,
            parsedResult = ticketParsingState,
            onAnalyzeTicket = { uri -> viewModel.analyzeTicket(uri) },
            onUseResult = { parsed ->
                prefilledTicketResult = parsed
                showScanTicketDialog = false
                showAddPurchaseDialog = true
            },
            onDismiss = {
                showScanTicketDialog = false
                viewModel.clearTicketParsingState()
            }
        )
    }

    if (showAudioDictationDialog) {
        AudioDictationDialog(
            onDismiss = { showAudioDictationDialog = false },
            onApplyDictation = { parsed ->
                prefilledTicketResult = parsed
                showAudioDictationDialog = false
                showAddPurchaseDialog = true
            }
        )
    }

    if (showDownloadApkDialog) {
        DownloadApkDialog(onDismiss = { showDownloadApkDialog = false })
    }
}
