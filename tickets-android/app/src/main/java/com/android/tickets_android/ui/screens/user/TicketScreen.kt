package com.android.tickets_android.ui.screens.user

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.tickets_android.api.TicketService
import com.android.tickets_android.model.Ticket
import com.android.tickets_android.model.UserManager
import com.android.tickets_android.network.RetrofitClient
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

// Pantlla principal de la sección de tickets del usuario
@Composable
fun TicketScreen() {
    //Instancia del servicio de tickets
    val ticketService = RetrofitClient.instance.create(TicketService::class.java)
    // Variable para mostrar el diálogo de filtro
    val showFilterDialog = remember { mutableStateOf(false) }
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

    // Se ejecuta para cargar los tickets
    // al principio o cuando se cambia el criterio de orden
    LaunchedEffect(key1 = currentPage, key2 = sortCriteria) {
        isLoading = true
        val userId = UserManager.userId
        try {
            val response = ticketService.getTicketsByUserId(
                userId,
                page = currentPage,
                size = 10,
                sort = listOf(sortCriteria)
            )
            if (response.isSuccessful && response.body() != null) {
                if (currentPage == 0) tickets = listOf()
                tickets = tickets + (response.body()?.content
                    ?: emptyList()) // Añadir los nuevos eventos a la lista existente
            } else {
                errorMessage = response.message()
            }
        } catch (e: Exception) {
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }

    Log.i("UserTicketsScreen", "Tickets: $tickets")

    // Composable para mostrar los eventos paginados
    if (!tickets.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
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
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text("Cargar más")
                    }
                }
            }

            // Botón flotante para filtrar
            FloatingActionButton(
                onClick = { showFilterDialog.value = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        bottom = 82.dp,
                        end = 16.dp
                    )
            ) {
                Icon(Icons.Filled.FilterList, contentDescription = "Filtro")
            }

            // Botón flotamte comprar ticket
            FloatingActionButton(
                onClick = { /*TODO*/ },
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
                        currentPage = 0  // Restablecer a la primera página
                    }
                }
            }
        }
    }
    // Si no hay tickets y no se está cargando, mostrar un mensaje
    // de que no hay mas tickets disponibles
    if (tickets.isEmpty() && !isLoading) {
        Text(
            "No hay entradas disponibles", modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
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
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { showDialog = true },
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = ticket.event.name, style = MaterialTheme.typography.h2)
            Text(text = ticket.uuid, style = MaterialTheme.typography.h5)
            if (showDialog) {
                showTicketQR(ticket, onDismissRequest = { showDialog = false })
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
