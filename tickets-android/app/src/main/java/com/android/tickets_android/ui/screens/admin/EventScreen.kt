package com.android.tickets_android.ui.screens.admin

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tickets_android.R
import com.android.tickets_android.api.EventService
import com.android.tickets_android.model.Event
import com.android.tickets_android.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle


// Pantlla principal de la sección de eventos del usuario
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AdminEventScreen() {
    val eventService = RetrofitClient.instance.create(EventService::class.java)
    var events by remember { mutableStateOf(listOf<Event>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentPage by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    val showFilterDialog = remember { mutableStateOf(false) }
    val showAddEventDialog = remember { mutableStateOf(false) }
    var sortCriteria by remember { mutableStateOf("name,asc") }

    // Function to load events
    fun loadEvents() {
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = eventService.getAllEvents(
                    page = currentPage,
                    size = 10,
                    sort = listOf(sortCriteria)
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        if (currentPage == 0) events = listOf()
                        events = events + (response.body()?.content ?: emptyList())
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
        loadEvents()
    }

    Log.i("UserEventsScreen", "Events: $events")

    Box(modifier = Modifier.fillMaxSize()) {
        if (events.isNotEmpty()) {
            LazyColumn {
                items(events) { event ->
                    EventCard(event = event, onDelete = { event ->
                        CoroutineScope(Dispatchers.Main).launch {
                            val deleted = deleteEvent(event.id)
                            if (deleted) {
                                loadEvents()
                            }
                        }
                    })
                }
                item {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier)
                    }
                }
                item {
                    Button(
                        onClick = { currentPage++ },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colorResource(id = R.color.light_blue)
                        ),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Cargar más")
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showFilterDialog.value = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.FilterList, contentDescription = "Filtro")
        }

        if (showFilterDialog.value) {
            ShowEventFilterDialog(
                showFilterDialog = showFilterDialog,
                currentSort = sortCriteria
            ) { newSortCriteria ->
                if (sortCriteria != newSortCriteria) {
                    sortCriteria = newSortCriteria
                    currentPage = 0
                    loadEvents()
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddEventDialog.value = true },
            backgroundColor = colorResource(id = R.color.light_blue),
            modifier = Modifier
                .padding(bottom = 16.dp, end = 16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar")
        }

        if (showAddEventDialog.value) {
            ShowAddEventDialog(
                showAddEventDialog = showAddEventDialog,
                onEventAdded = {
                    currentPage = 0
                    loadEvents()
                }
            )
        }

        if (events.isEmpty() && !isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay eventos disponibles", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

suspend fun deleteEvent(eventId: Long): Boolean {
    val eventService = RetrofitClient.instance.create(EventService::class.java)
    return withContext(Dispatchers.IO) {
        try {
            val response = eventService.deleteEvent(eventId)
            if (response.isSuccessful) {
                Log.i("UserTicketsScreen", "Evento eliminado correctamente: ${response}")
                true
            } else {
                Log.e("UserTicketsScreen", "Error al eliminar el evento: ${response}")
                false
            }
        } catch (e: Exception) {
            Log.e("UserTicketsScreen", "Error al eliminar el evento", e)
            false
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ShowAddEventDialog(showAddEventDialog: MutableState<Boolean>, onEventAdded: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { showAddEventDialog.value = false },
        title = { Text("Añadir evento nuevo") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(id = R.color.dark_blue),
                        focusedLabelColor = colorResource(id = R.color.dark_blue),
                        unfocusedLabelColor = Color.Gray,
                        unfocusedBorderColor = colorResource(id = R.color.blue)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(id = R.color.dark_blue),
                        focusedLabelColor = colorResource(id = R.color.dark_blue),
                        unfocusedLabelColor = Color.Gray,
                        unfocusedBorderColor = colorResource(id = R.color.blue)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = place,
                    onValueChange = { place = it },
                    label = { Text("Lugar") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(id = R.color.dark_blue),
                        focusedLabelColor = colorResource(id = R.color.dark_blue),
                        unfocusedLabelColor = Color.Gray,
                        unfocusedBorderColor = colorResource(id = R.color.blue)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(id = R.color.dark_blue),
                        focusedLabelColor = colorResource(id = R.color.dark_blue),
                        unfocusedLabelColor = Color.Gray,
                        unfocusedBorderColor = colorResource(id = R.color.blue)
                    ),
                    label = { Text("Fecha (01/01/2000 12:00)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                addEvent(
                    name = name,
                    description = description,
                    place = place,
                    date = date
                ) {
                    onEventAdded()  // Llamar a la función para recargar eventos
                    showAddEventDialog.value = false
                }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.light_blue)
                )
            ) {
                Text("Añadir")
            }
        },
        dismissButton = {
            Button(
                onClick = { showAddEventDialog.value = false },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.light_blue)
                )
            ) {
                Text("Cancelar")
            }
        }
    )
}

fun addEvent(
    name: String,
    description: String,
    place: String,
    date: String,
    onEventAdded: () -> Unit
) {
    val eventService = RetrofitClient.instance.create(EventService::class.java)
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val dateTime = LocalDateTime.parse(date, formatter)

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = eventService.createEvent(Event(name, description, place, dateTime.toString()))
            Log.d("UserTicketsResponse", "Received Response: $response")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body() != null) {
                    Log.i("UserTicketsScreen", "Evento añadido correctamente: ${response}")
                    // Llamar a la función de recarga de eventos
                    onEventAdded()
                } else {
                    Log.e("UserTicketsScreen", "Error al añadir el evento: ${response}")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.e("UserTicketsScreen", "Error al añadir el evento", e)
            }
        }
    }
}

// Pop-up para filtrar y ordenar los eventos
@Composable
fun ShowEventFilterDialog(
    showFilterDialog: MutableState<Boolean>,
    currentSort: String,
    updateSortCriteria: (String) -> Unit
) {
    var tempSortCriteria by remember { mutableStateOf(currentSort) } // Estado temporal para gestionar el criterio de ordenación dentro del diálogo

    AlertDialog(
        onDismissRequest = { showFilterDialog.value = false },
        title = { Text("Filtrar y ordenar") },
        text = {
            Column {
                EventSortingOption("Nombre Ascendente", "name,asc", tempSortCriteria) {
                    tempSortCriteria = it
                }
                EventSortingOption("Nombre Descendente", "name,desc", tempSortCriteria) {
                    tempSortCriteria = it
                }
                EventSortingOption("Lugar Ascendente", "place,asc", tempSortCriteria) {
                    tempSortCriteria = it
                }
                EventSortingOption("Lugar Descendente", "place,desc", tempSortCriteria) {
                    tempSortCriteria = it
                }
            }
        },
        // Botón para confirmar la selección
        confirmButton = {
            Button(onClick = {
                updateSortCriteria(tempSortCriteria) // Actualiza el estado global solo cuando se confirma la selección
                showFilterDialog.value = false
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.light_blue)
                )) {
                Text("Aceptar")
            }
        },
        // Botón para cancelar la selección
        dismissButton = {
            Button(
                onClick = { showFilterDialog.value = false },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.light_blue)
                )
            ) {
                Text("Cancelar")
            }
        }
    )
}

// Composable para mostrar las opciones de ordenación
@Composable
fun EventSortingOption(
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

// Composable para mostrar un evento en un Card
@Composable
fun EventCard(event: Event, onDelete: (Event) -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val dateTime = LocalDateTime.parse(event.date, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val formattedDate = dateTime.format(formatter)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp,
        backgroundColor = colorResource(id = R.color.blue)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.h5.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Filled.Delete,
                    tint = Color.Red,
                    contentDescription = "Eliminar",
                    modifier = Modifier
                        .clickable { onDelete(event) }
                        .padding(8.dp)
                )
            }
            Text(
                text = event.description,
                style = MaterialTheme.typography.body1,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Lugar: ${event.place}",
                style = MaterialTheme.typography.caption,
                color = Color.DarkGray
            )
            Text(
                text = "Fecha: $formattedDate",
                style = MaterialTheme.typography.caption,
                color = Color.DarkGray
            )
        }
    }
}