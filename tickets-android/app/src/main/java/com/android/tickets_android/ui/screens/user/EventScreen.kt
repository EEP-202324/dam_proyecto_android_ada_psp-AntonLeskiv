package com.android.tickets_android.ui.screens.user

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import com.android.tickets_android.api.EventService
import com.android.tickets_android.model.Event
import com.android.tickets_android.network.RetrofitClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Pantlla principal de la sección de eventos del usuario
@Composable
fun UserEventScreen() {
    // Instancia del servicio de eventos
    val eventService = RetrofitClient.instance.create(EventService::class.java)
    // Variable para almacenar los eventos
    var events by remember { mutableStateOf(listOf<Event>()) }
    // Variable para mostrar mensajes de error
    var errorMessage by remember { mutableStateOf<String?>(null) }
    // Variable para almacenar la página actual
    var currentPage by remember { mutableStateOf(0) }
    // Variable para decir si se está cargando la información
    var isLoading by remember { mutableStateOf(false) }
    // Variable para mostrar el diálogo de filtro
    val showFilterDialog = remember { mutableStateOf(false) }
    // Variable para almacenar el criterio predeterminado de orden
    var sortCriteria by remember { mutableStateOf("name,asc") }

    // Se ejecuta para cargar los eventos
    // al principio o cuando se cambia el criterio de orden
    LaunchedEffect(key1 = currentPage, key2 = sortCriteria) {
        isLoading = true
        try {
            val response = eventService.getAllEvents(
                page = currentPage,
                size = 10,
                sort = listOf(sortCriteria)
            )
            if (response.isSuccessful && response.body() != null) {
                if (currentPage == 0) events = listOf()
                events = events + (response.body()?.content
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

    Log.i("UserEventsScreen", "Events: $events")

    // Composable para mostrar los eventos paginados
    if (!events.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                // Mostrar cada evento en un Card
                items(events) { event ->
                    EventCard(event)
                }

                // Mostrar un indicador de carga si se está cargando
                item {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier)
                    }
                }

                // Botón para cargar más eventos
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
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.FilterList, contentDescription = "Filtro")
            }

            // Mostrar el pop-up de filtro si showFilterDialog es true
            if (showFilterDialog.value) {
                ShowEventFilterDialog(
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

    // Si no hay eventos y no se está cargando, mostrar un mensaje
    // de que no hay mas eventos disponibles
    if (events.isEmpty() && !isLoading) {
        Text(
            "No hay eventos disponibles", modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
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
fun EventCard(event: Event) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val dateTime = LocalDateTime.parse(event.date.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val formattedDate = dateTime.format(formatter)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.name, style = MaterialTheme.typography.h2)
            Text(text = event.description, style = MaterialTheme.typography.h5)
            Text(text = "Lugar: ${event.place}", style = MaterialTheme.typography.h6)
            Text(text = "Fecha: $formattedDate", style = MaterialTheme.typography.h6)
        }
    }
}