package com.tiketeer.tiketeer.domain.file.controller

import com.tiketeer.tiketeer.domain.file.dto.UploadFileCommandDto
import com.tiketeer.tiketeer.domain.file.dto.UploadFileRequestDto
import com.tiketeer.tiketeer.domain.file.strategy.FileStorageStrategy
import com.tiketeer.tiketeer.domain.file.usecase.UploadFileUseCase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.activation.MimetypesFileTypeMap

@RestController
@RequestMapping("/files")
class FileController @Autowired constructor(
    private val fileStorageStrategy: FileStorageStrategy,
    private val uploadFileUseCase: UploadFileUseCase
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFile(
        @RequestPart("file") fileMono: Mono<FilePart>,
        @RequestPart("dto") dtoMono: Mono<UploadFileRequestDto>
    ): Mono<ResponseEntity<Void>> {
        return fileMono.zipWith(dtoMono) { file, dto ->
            UploadFileCommandDto(
                fileName = dto.fileName,
                signature = dto.signature,
                file = file
            )
        }.flatMap { commandDto ->
            uploadFileUseCase.uploadFile(commandDto)
        }.then(Mono.just(ResponseEntity.ok().build()))
    }

    @GetMapping("/{fileId}")
    fun getFile(@PathVariable fileId: String): Mono<ResponseEntity<Flux<DataBuffer>>> {
        val fileTypeMap = MimetypesFileTypeMap()
        val mimeType = fileTypeMap.getContentType(fileId)

        return Mono.just(
            ResponseEntity.ok()
                .contentType(MediaType.valueOf(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$fileId\"")
                .body(fileStorageStrategy.retrieveFile(fileId))
        )
    }


}
