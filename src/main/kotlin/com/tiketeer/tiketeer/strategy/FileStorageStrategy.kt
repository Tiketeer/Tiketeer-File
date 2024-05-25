package com.tiketeer.tiketeer.strategy

import com.tiketeer.tiketeer.StorageFile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File


interface FileStorageStrategy {
    fun uploadFile(file: StorageFile): Mono<String>
    fun uploadFiles(files: List<StorageFile>): Flux<String>
    fun retrieveFile(fileId: String): Mono<ByteArray>
}