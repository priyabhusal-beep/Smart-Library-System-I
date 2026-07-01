package com.sample.smartlibrarysystem.model

data class UserModel(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val address: String = "",
    val contact: String = "",
    var imageUrl : String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "email" to email,
            "address" to address,
            "contact" to contact,
            "imageUrl" to imageUrl,
        )
    }
}