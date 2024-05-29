package com.tiketeer.tiketeer.domain.file.usecase

import com.tiketeer.tiketeer.StorageFile
import com.tiketeer.tiketeer.domain.file.dto.UploadFileCommandDto
import com.tiketeer.tiketeer.domain.file.strategy.FileStorageStrategy
import com.tiketeer.tiketeer.domain.sign.SignService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UploadFileUseCase @Autowired constructor(
    private val fileStorageStrategy: FileStorageStrategy,
    private val signService: SignService
) {
    fun uploadFile(dto: UploadFileCommandDto): Mono<Void> {
        return Mono.defer {
            if (!signService.verify(dto.fileName, dto.signature)) {
                return@defer Mono.error<Void>(RuntimeException("Invalid Signature"))
            }

            val storageFile = StorageFile(
                dto.file,
                fileName = dto.fileName + "_" + dto.file.filename()
            )
            fileStorageStrategy.uploadFile(storageFile)
                .then()
        }
    }

}