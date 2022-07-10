package com.example.roadmaintenance.services

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class FileManager(
    private val context: Context
) {

    private val contentResolver = context.contentResolver
    private val externalFilesDir = context.getExternalFilesDir(null)
    private lateinit var savedFile: File

    fun copyFromSource(uri: Uri): File {

        if (externalFilesDir?.exists() == false)
            externalFilesDir.mkdirs()

        val fileName = File(uri.path).name

        val inputStream = contentResolver.openInputStream(uri) ?: kotlin.run {
            throw RuntimeException("Cannot open for reading $uri")
        }

        savedFile = if (uri.toString().endsWith(".xlsx"))
            File(externalFilesDir!!.path + "/" + fileName)
        else
            File(externalFilesDir!!.path + "/" + fileName + ".xlsx")

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

    fun removeAll() {
        if (context.getExternalFilesDir(null)?.exists() == true)
            context.getExternalFilesDir(null)?.listFiles()?.forEach {
                it.delete()
            }
    }

}