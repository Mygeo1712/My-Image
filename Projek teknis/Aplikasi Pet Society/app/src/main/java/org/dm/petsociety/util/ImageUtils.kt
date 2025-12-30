// File: org.dm.petsociety.util.ImageUtils.kt (REVISI)

package org.dm.petsociety.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.util.Log
import java.io.File

// Fungsi utilitas untuk mengonversi URI ke Bitmap
fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val resolver = context.contentResolver
        MediaStore.Images.Media.getBitmap(resolver, uri)
    } catch (e: Exception) {
        Log.e("ImageUtils", "Gagal mengonversi URI ke Bitmap: ${e.message}")
        null
    }
}

// Fungsi utilitas untuk mendapatkan URI file sementara untuk kamera
fun createTempUri(context: Context): Uri {
    val tempFile = File.createTempFile(
        "temp_image",
        ".jpg",
        context.externalCacheDir
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider", // <-- Pastikan Anda memiliki FileProvider di Manifest
        tempFile
    )
}


// Fungsi utilitas untuk mendapatkan nama pengguna yang ditampilkan
fun getUserDisplayName(context: Context): String {
    val user = Firebase.auth.currentUser
    if (user != null) {
        return user.displayName ?: user.email?.substringBefore('@') ?: "Pengguna"
    }
    return "Pet Owner"
}