package com.tiketeer.tiketeer.domain.file.strategy
import com.tiketeer.tiketeer.StorageFile
import com.tiketeer.tiketeer.domain.file.strategy.FileUploadStrategy
import com.tiketeer.tiketeer.domain.file.util.LocalFileSystemUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.nio.file.Path
import java.util.*


class LocalFileUploadStrategy(private val localFileSystemUtils: LocalFileSystemUtils
): FileUploadStrategy {

    private val logger = KotlinLogging.logger {}

    override fun uploadFile(file: StorageFile): Mono<String> {
        val fileName = UUID.randomUUID().toString() + "_" + file.fileName
        val destinationPath = localFileSystemUtils.getPath(fileName)
        val fileUrl = localFileSystemUtils.generateFileUrl(fileName)

        saveFileAsync(file.file, destinationPath)
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(
                { logger.debug { "File successfully saved to $destinationPath" } },
                { error -> logger.error(error) { "Failed to save file to $destinationPath" } }
            )
        return Mono.just(fileUrl)
    }

    override fun uploadFiles(files: List<StorageFile>): Flux<String> {
        return Flux.fromIterable(files)
            .flatMap { file ->
                uploadFile(file).thenReturn(file.fileName)
            }
    }

    private fun saveFileAsync(filePart: FilePart, destinationPath: Path): Mono<Void> {
        return filePart.transferTo(destinationPath)
    }
}