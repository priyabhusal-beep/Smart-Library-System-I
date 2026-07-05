package com.sample.smartlibrarysystem.model

data class RentedBookModel(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val imageUrl: String = "",
    val paymentMethod: String = "",
    val rentFee: Int = 0,
    val status: String = "",
    val rentedAt: Long = System.currentTimeMillis()
)