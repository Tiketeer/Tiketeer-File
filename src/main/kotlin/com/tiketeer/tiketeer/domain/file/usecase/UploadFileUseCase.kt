package com.tiketeer.tiketeer.domain.file.usecase

import com.tiketeer.tiketeer.StorageFile
import com.tiketeer.tiketeer.domain.file.dto.UploadFileCommandDto
import com.tiketeer.tiketeer.domain.file.strategy.FileUploadStrategy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UploadFileUseCase @Autowired constructor(
    private val fileUploadStrategy: FileUploadStrategy,
) {
    fun uploadFile(dto: UploadFileCommandDto): Mono<String> {
        return Mono.just(dto).flatMap { dto ->

            val storageFile = StorageFile(
                dto.file,
                fileName = dto.fileName
            )
            fileUploadStrategy.uploadFile(storageFile)
        }

    }

}