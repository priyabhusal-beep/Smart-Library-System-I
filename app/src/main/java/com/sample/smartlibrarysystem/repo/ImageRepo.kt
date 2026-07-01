package com.sample.smartlibrarysystem.repo

import android.content.Context
import android.net.Uri

interface ImageRepo {
    fun uploadImage(
        context: Context,
        imageUri: Uri,
        callback: (Boolean, String) -> Unit
    )
}