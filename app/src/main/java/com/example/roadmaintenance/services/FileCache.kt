package com.example.roadmaintenance.services

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class FileCache(
    private val context: Context
) {

    private val contentResolver = context.contentResolver
    private val cacheLocation = context.cacheDir
    private lateinit var savedFile: File

    fun copyFromSource(uri: Uri): File {
        removeAll()
        if (!cacheLocation.exists())
            cacheLocation.mkdirs()

        val fileName = File(uri.path).name

        val inputStream = contentResolver.openInputStream(uri) ?: kotlin.run {
            throw RuntimeException("Cannot open for reading $uri")
        }

        savedFile = File(cacheLocation.path + "/" + fileName)

        if (!savedFile.exists()) savedFile.createNewFile()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i("High-Sdk", Build.VERSION.SDK_INT.toString())
            Files.copy(inputStream, savedFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        } else {
            Log.i("Low-Sdk", Build.VERSION.SDK_INT.toString())
            inputStream.use { input ->
                savedFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

        return savedFile
    }

    private fun removeAll() {
        if (context.cacheDir.exists())
            context.cacheDir.deleteRecursively()
    }

}