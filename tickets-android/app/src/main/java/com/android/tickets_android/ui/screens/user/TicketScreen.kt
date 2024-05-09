package com.android.tickets_android.ui.screens.user

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.android.tickets_android.api.EventService
import com.android.tickets_android.api.TicketService
import com.android.tickets_android.model.Event
import com.android.tickets_android.model.Ticket
import com.android.tickets_android.model.UserManager
import com.android.tickets_android.model.UserManager.userId
import com.android.tickets_android.network.RetrofitClient
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// Pantlla principal de la sección de tickets del usuario
@Composable
fun UserTicketScreen() {
    //Instancia del servicio de tickets
    val ticketService = RetrofitClient.instance.create(TicketService::class.java)
    // Variable para mostrar el diálogo de filtro
    val showFilterDialog = remember { mutableStateOf(false) }
    // Variable para mostrar el diálogo de compra de tickets
    val showPurchaseDialog = remember { mutableStateOf(false) }
    // Variable para almacenar los tickets
    var tickets by remember { mutableStateOf(listOf<Ticket>()) }
    // Variable para mostrar mensajes de error
    var errorMessage by remember { mutableStateOf<String?>(null) }
    // Variable para almacenar la página actual
    var currentPage by remember { mutableStateOf(0) }
    // Variable para decir si se está cargando la información
    var isLoading by remember { mutableStateOf(false) }
    // Variable para almacenar el criterio predeterminado de orden
    var sortCriteria by remember { mutableStateOf("uuid,asc") }

    // Function to load tickets
    fun loadTickets() {
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ticketService.getTicketsByUserId(
                    UserManager.userId,
                    page = currentPage,
                    size = 10,
                    sort = listOf(sortCriteria)
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        if (currentPage == 0) tickets = listOf()
                        tickets = tickets + (response.body()?.content ?: emptyList())
                    } else {
                        errorMessage = response.message()
                    }
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(key1 = currentPage, key2 = sortCriteria) {
        loadTickets()
    }

    Log.i("UserTicketsScreen", "Tickets: $tickets")

    Box(modifier = Modifier.fillMaxSize()) {
        if (tickets.isNotEmpty()) {
            LazyColumn {
                // Mostrar cada ticket en un Card
                items(tickets) { ticket ->
                    TicketCard(ticket)
                }

                // Mostrar un indicador de carga si se está cargando
                item {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier)
                    }
                }

                // Botón para cargar más tickets
                item {
                    Button(
                        onClick = { currentPage++ },
                        enabled = !isLoading,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Cargar más")
                    }
                }
            }
        }

        // Botón flotante para filtrar
        FloatingActionButton(
            onClick = { showFilterDialog.value = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 82.dp, end = 16.dp)
        ) {
            Icon(Icons.Filled.FilterList, contentDescription = "Filtro")
        }

        // Botón flotamte comprar ticket
        FloatingActionButton(
            onClick = { showPurchaseDialog.value = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar")
        }

        // Mostrar el pop-up de filtro si showFilterDialog es true
        if (showFilterDialog.value) {
            ShowTicketFilterDialog(
                showFilterDialog = showFilterDialog,
                currentSort = sortCriteria
            ) { newSortCriteria ->
                if (sortCriteria != newSortCriteria) {
                    sortCriteria = newSortCriteria
                    currentPage = 0 // Restablecer a la primera página
                    loadTickets()
                }
            }
        }

        // Mostrar el pop-up de filtro si showFilterDialog es true
        if (showPurchaseDialog.value) {
            ShowTicketPurchaseDialog(
                showPurchaseDialog = showPurchaseDialog,
                onTicketPurchased = {
                    currentPage = 0
                    loadTickets()
                }
            )
        }

        if (tickets.isEmpty() && !isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay entradas disponibles", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ShowTicketPurchaseDialog(
    showPurchaseDialog: MutableState<Boolean>,
    onTicketPurchased: () -> Unit
) {
    var eventService = RetrofitClient.instance.create(EventService::class.java)
    var events by remember { mutableStateOf(listOf<Event>()) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val response = eventService.getAllEvents(0, 100, listOf("name,asc"))
            if (response.isSuccessful && response.body() != null) {
                events = response.body()?.content ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("UserTicketsScreen", "Error al cargar los eventos", e)
        }
    }

    // UI del diálogo
    AlertDialog(
        onDismissRequest = { showPurchaseDialog.value = false },
        title = { Text("Comprar Ticket") },
        text = {
            Column {
                // Desplegable para seleccionar evento
                Box {
                    Text(
                        text = selectedEvent?.name ?: "Seleccionar Evento",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { expanded = true }
                            .background(MaterialTheme.colors.surface)
                            .padding(16.dp)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        events.forEach { event ->
                            DropdownMenuItem(onClick = {
                                selectedEvent = event
                                expanded = false
                            }) {
                                Text(text = event.name)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                // Lógica para comprar el ticket
                selectedEvent?.let { event ->
                    purchaseTicket(event, onTicketPurchased)
                }
                showPurchaseDialog.value = false
            }) {
                Text("Comprar")
            }
        },
        dismissButton = {
            Button(onClick = { showPurchaseDialog.value = false }) {
                Text("Cancelar")
            }
        }
    )
}

// Función para realizar la compra del ticket
fun purchaseTicket(event: Event, onTicketPurchased: () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val ticketService = RetrofitClient.instance.create(TicketService::class.java)
            val response = ticketService.createTicket(UserManager.userId, event.id).execute()
            if (response.isSuccessful) {
                Log.i("PurchaseTicket", "Compra exitosa para el evento: ${event.name}")
                onTicketPurchased()
            } else {
                Log.e("PurchaseTicket", "Error en la compra: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("PurchaseTicket", "Error en la red o el servidor", e)
        }
    }
}

// Pop-up para filtrar y ordenar los tickets
@Composable
fun ShowTicketFilterDialog(
    showFilterDialog: MutableState<Boolean>,
    currentSort: String,
    updateSortCriteria: (String) -> Unit
) {
    var tempSortCriteria by remember { mutableStateOf(currentSort) }

    AlertDialog(
        onDismissRequest = { showFilterDialog.value = false },
        title = { Text("Filtrar y ordenar") },
        text = {
            Column {
                TicketSortingOption(
                    label = "UUID Ascendente",
                    sortValue = "uuid,asc",
                    currentSort = tempSortCriteria,
                    onSelectionChanged = { newSort ->
                        tempSortCriteria = newSort
                    }
                )
                TicketSortingOption(
                    label = "UUID Descendente",
                    sortValue = "uuid,desc",
                    currentSort = tempSortCriteria,
                    onSelectionChanged = { newSort ->
                        tempSortCriteria = newSort
                    }
                )
                TicketSortingOption(
                    label = "Nombre Evento Ascendente",
                    sortValue = "event.name,asc",
                    currentSort = tempSortCriteria,
                    onSelectionChanged = { newSort ->
                        tempSortCriteria = newSort
                    }
                )
                TicketSortingOption(
                    label = "Nombre Evento Descendente",
                    sortValue = "event.name,desc",
                    currentSort = tempSortCriteria,
                    onSelectionChanged = { newSort ->
                        tempSortCriteria = newSort
                    }
                )
            }
        },
        // Botón para confirmar la selección
        confirmButton = {
            Button(onClick = {
                updateSortCriteria(tempSortCriteria)
                showFilterDialog.value = false
            }) {
                Text("Aceptar")
            }
        },
        // Botón para cancelar la selección
        dismissButton = {
            Button(onClick = { showFilterDialog.value = false }) {
                Text("Cancelar")
            }
        }
    )
}

// Composable para mostrar las opciones de ordenación
@Composable
fun TicketSortingOption(
    label: String,
    sortValue: String,
    currentSort: String,
    onSelectionChanged: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = currentSort == sortValue,
            onClick = { onSelectionChanged(sortValue) }
        )
        Text(text = label, modifier = Modifier.clickable { onSelectionChanged(sortValue) })
    }
}

// Composable para mostrar un ticket en un Card
@Composable
fun TicketCard(ticket: Ticket) {
    var showQRDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { showQRDialog = true },
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = ticket.event.name, style = MaterialTheme.typography.h2)
            Text(text = ticket.uuid, style = MaterialTheme.typography.h6)
            if (showQRDialog) {
                showTicketQR(ticket, onDismissRequest = { showQRDialog = false })
            }
        }
    }
}

// Pop-up para mostrar el código QR del ticket
@Composable
fun showTicketQR(ticket: Ticket, onDismissRequest: () -> Unit) {
    val bitmap = generateQRCode(ticket.uuid)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onDismissRequest) {
                    Text("Cerrar")
                }
            }
        },
        text = {
            Column {
                Text(text = "Evento: ${ticket.event.name}")
                Text(text = "ID del ticket: ${ticket.uuid}")
                Spacer(modifier = Modifier.height(16.dp))
                bitmap?.let {
                    Image(bitmap = it.asImageBitmap(), contentDescription = "Código QR del Ticket")
                }
            }
        }
    )
}

// Función para generar un código QR a partir de un texto
fun generateQRCode(text: String): Bitmap? {
    val width = 1000 // ancho del código QR
    val height = 1000 // alto del código QR
    val qrCodeWriter = QRCodeWriter()
    try {
        val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                )
            }
        }
        return bitmap
    } catch (e: Exception) {
        Log.e("UserTicketsScreen", "Error al generar el código QR", e)
    }
    return null
}
