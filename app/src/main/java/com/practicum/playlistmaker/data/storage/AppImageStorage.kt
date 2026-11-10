package com.practicum.playlistmaker.data.storage

import android.content.Context
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.practicum.playlistmaker.data.ImageStorage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class AppImageStorage(
    private val context: Context,
    private val directoryName: String,
) : ImageStorage {

    override fun saveImage(uri: String): String? {
        val sourceUri = uri.toUri()
        val directory = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir,
            directoryName,
        )
        if (!directory.exists() && !directory.mkdirs()) return null

        val extension = context.contentResolver.getType(sourceUri)
            ?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
            ?: DEFAULT_EXTENSION
        val file = File(directory, "${UUID.randomUUID()}.$extension")

        return try {
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
            inputStream.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
            file.absolutePath
        } catch (e: IOException) {
            file.delete()
            null
        } catch (e: SecurityException) {
            file.delete()
            null
        }
    }

    companion object {
        private const val DEFAULT_EXTENSION = "jpg"
    }
}
