package com.sample.smartlibrarysystem.repo

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

class ImageRepoImp : ImageRepo {

    override fun uploadImage(
        context: Context,
        imageUri: Uri,
        callback: (Boolean, String) -> Unit
    ) {
        MediaManager.get()
            .upload(imageUri)
            .unsigned("library_unsigned")
            .option("resource_type", "image")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: MutableMap<*, *>?) {
                    val imageUrl = resultData?.get("secure_url") as? String

                    if (!imageUrl.isNullOrEmpty()) {
                        callback(true, imageUrl)
                    } else {
                        callback(false, "Cloudinary URL not found")
                    }
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    callback(false, error?.description ?: "Cloudinary upload failed")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    callback(false, error?.description ?: "Upload rescheduled")
                }
            })
            .dispatch()
    }
}