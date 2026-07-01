package com.sample.smartlibrarysystem.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.sample.smartlibrarysystem.repo.ImageRepo

class ImageViewModel(private val repo: ImageRepo) : ViewModel() {

    fun uploadImage(
        context: Context,
        imageUri: Uri,
        callback: (Boolean, String) -> Unit
    ) {
        repo.uploadImage(context, imageUri, callback)
    }
}