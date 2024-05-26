package com.tiketeer.tiketeer.strategy

import com.tiketeer.tiketeer.StorageFile
import org.springframework.core.io.buffer.DataBuffer
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface FileStorageStrategy {
    fun uploadFile(file: StorageFile): Mono<String>
    fun uploadFiles(files: List<StorageFile>): Flux<String>
    fun retrieveFile(fileId: String): Flux<DataBuffer>
}