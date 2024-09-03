package com.tiketeer.tiketeer.domain.file.strategy

import com.tiketeer.tiketeer.StorageFile
import org.springframework.core.io.buffer.DataBuffer
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface FileUploadStrategy {
    fun uploadFile(file: StorageFile): Mono<String>
    fun uploadFiles(files: List<StorageFile>): Flux<String>
}