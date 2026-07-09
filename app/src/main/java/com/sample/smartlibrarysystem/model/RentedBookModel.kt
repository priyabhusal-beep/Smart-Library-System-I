package com.sample.smartlibrarysystem.model

data class RentedBookModel(
    val rentId: String = "",
    val userId: String = "",
    val title: String = "",
    val author: String = "",
    val type: String = "",
    val rating: Double = 0.0,
    val imageUrl: String = "",
    val summary: String = "",
    val rentedAt: Long = 0L,
    val paymentMethod: String = "Not selected",
    val rentFee: Int = 0,
    val status: String = "Pending"
)