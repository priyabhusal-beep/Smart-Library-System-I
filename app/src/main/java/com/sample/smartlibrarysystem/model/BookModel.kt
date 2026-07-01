package com.sample.smartlibrarysystem.model

data class BookModel(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val type: String = "",
    val rating: Double = 0.0,
    val isAvailable: Boolean = true,
    val imageUrl: String = "",
    val summary : String = ""
)