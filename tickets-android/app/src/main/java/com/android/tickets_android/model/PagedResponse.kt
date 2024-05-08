package com.android.tickets_android.model

data class PagedResponse<T>(
    val content: List<T>,
    val totalElements: Int,
    val totalPages: Int,
    val number: Int  // Número de página actual
)
