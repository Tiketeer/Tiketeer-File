package com.tiketeer.tiketeer.strategy

import com.tiketeer.tiketeer.StorageFile
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.io.FileSystemResource
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

private val logger = KotlinLogging.logger {}


class LocalFileStorageStrategy(private val localStoragePath: String) : FileStorageStrategy{

    private val absolutePath: Path

    init {
        this.absolutePath = getLocalStoragePath();
    }

    private fun getLocalStoragePath(): Path {
        val projectPath: String = FileSystemResource("").file.absolutePath;
        val storagePath = Paths.get(projectPath).resolve(localStoragePath)
        try {
            if (!Files.exists(storagePath)) {
                Files.createDirectories(storagePath)
            }
        } catch (e: IOException) {
            logger.error { "Failed to create directory at $storagePath" }
            throw RuntimeException("File upload failed")
        }
        return storagePath
    }

    override fun retrieveFile(fileId: String): StorageFile {
        try{
            val filePath = absolutePath.resolve(fileId)
            return StorageFile(
                    fileName = fileId,
                    fileData = Files.readAllBytes(filePath)
            )
        } catch(e: IOException) {
            logger.error {"Failed to retrieve file $fileId at $absolutePath"}
            throw RuntimeException("File retrieval failed")
        }
    }

    override fun uploadFile(file: StorageFile): String {
        try {
            val fileName: String = file.fileName
            val destinationPath = absolutePath.resolve(fileName)
            Files.write(destinationPath, file.fileData)
            logger.debug {"Uploaded file $fileName at $destinationPath"}
            return fileName
        } catch(e: IOException) {
            logger.error { "Failed to store file at $absolutePath" }
            throw RuntimeException("File upload failed")
        }
    }

    override fun uploadFiles(files: List<StorageFile>): List<String> {
        return files.map { uploadFile(it) }
    }
}