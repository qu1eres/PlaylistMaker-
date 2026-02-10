package com.example.verstka_last.playlist_create.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

class FileStorage(private val context: Context) {
    fun saveImageToPrivateStorage(filePath: File, fileName: String, uri: Uri) {
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "${fileName}.jpg")
        BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri)).compress(
            Bitmap.CompressFormat.JPEG, 90,
            FileOutputStream(file)
        )
    }
}