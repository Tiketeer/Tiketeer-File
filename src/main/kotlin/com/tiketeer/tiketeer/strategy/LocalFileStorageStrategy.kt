package com.tiketeer.tiketeer.strategy

import com.tiketeer.tiketeer.StorageFile
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


private val logger = KotlinLogging.logger {}

class LocalFileStorageStrategy(private val localStoragePath: String) : FileStorageStrategy {

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


    override fun retrieveFile(fileId: String): Flux<DataBuffer> {
        val filePath = absolutePath.resolve(fileId)
        println("retrieveFile: $filePath")

        return DataBufferUtils.read(
            filePath,
            DefaultDataBufferFactory(),
            4096
        ).subscribeOn(Schedulers.boundedElastic())
            .doOnError {
                logger.error { "Failed to retrieve file $fileId at $absolutePath" }
                throw RuntimeException("File retrieval failed")
            }
    }


    override fun uploadFile(file: StorageFile): Mono<String> {
        val fileName = file.fileName
        val destinationPath = absolutePath.resolve(fileName)

        return file.file.transferTo(destinationPath)
            .then(Mono.fromCallable {
                logger.debug { "Uploaded file $fileName at $destinationPath" }
                fileName
            })
            .subscribeOn(Schedulers.boundedElastic())
            .doOnError { e ->
                logger.error { "Failed to store file at $absolutePath: ${e.message}" }
                throw RuntimeException("File upload failed", e)
            }
    }


    override fun uploadFiles(files: List<StorageFile>): Flux<String> {
        return Flux.fromIterable(files)
            .flatMap { file ->
                uploadFile(file).thenReturn(file.fileName)
            }
    }
}