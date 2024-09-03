package com.tiketeer.tiketeer.domain.file.util

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Component
class LocalFileSystemUtils(
    @Value("\${custom.policy.local-storage-path}")
    val localStoragePath: String,

    @Value("\${custom.base-url}")
    val baseUrl: String
) {

    protected val logger = KotlinLogging.logger {}

    fun getPath(fileId: String): Path {
        return getAbsolutePath().resolve(fileId)
    }

    fun getAbsolutePath(): Path {
        val projectPath: String = FileSystemResource("").file.absolutePath
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

    fun generateFileUrl(fileName: String): String {
        return "$baseUrl/file/local/$fileName"
    }
}