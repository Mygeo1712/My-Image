// File: org.dm.petsociety.network.CloudinaryClient.kt

package org.dm.petsociety.network

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object CloudinaryClient {
    // !! PERINGATAN KRITIS: Ganti dengan kredensial Anda yang sebenarnya !!
    // CLOUD_NAME = dnplrrjsy
    // API_KEY dan API_SECRET adalah kunci yang Anda temukan di Dashboard.
    private const val CLOUD_NAME = "dnplrrjsy"
    private const val API_KEY = "166369425757628"
    private const val API_SECRET = "cJVePgF4JXE-kh6CVxhH_lTfvK8"
    private const val UPLOAD_PRESET = "petsociety_uploads" // Preset yang dibuat dan diset Unsigned

    fun init(context: Context) {
        val config = mapOf(
            "cloud_name" to CLOUD_NAME,
            "api_key" to API_KEY,
            "api_secret" to API_SECRET
        )
        try {
            MediaManager.init(context, config)
        } catch (e: IllegalStateException) {
            Log.w("CloudinaryClient", "MediaManager already initialized.")
        }
    }

    suspend fun uploadImage(bitmap: Bitmap): String = suspendCancellableCoroutine { continuation ->
        val baos = ByteArrayOutputStream()
        // Kompresi kualitas 80%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val imageBytes = baos.toByteArray()

        MediaManager.get().upload(imageBytes)
            .option("resource_type", "image")
            .option("upload_preset", UPLOAD_PRESET)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) { Log.d("Cloudinary", "Upload $requestId started.") }
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {} // Progress handling dihilangkan
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String
                    if (url != null) {
                        continuation.resume(url)
                    } else {
                        continuation.resumeWithException(RuntimeException("URL tidak ditemukan dari respons Cloudinary."))
                    }
                }
                override fun onError(requestId: String, error: ErrorInfo) {
                    Log.e("Cloudinary", "Upload error: ${error.description}")
                    continuation.resumeWithException(RuntimeException(error.description))
                }
                override fun onReschedule(requestId: String, error: ErrorInfo) {}
            })
            .dispatch()

        continuation.invokeOnCancellation {
            // Optional: Tambahkan logika pembatalan upload
        }
    }
}