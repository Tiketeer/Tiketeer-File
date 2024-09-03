package com.tiketeer.tiketeer.domain.file.controller

import com.tiketeer.tiketeer.domain.file.dto.UploadFileCommandDto
import com.tiketeer.tiketeer.domain.file.dto.UploadFileRequestDto
import com.tiketeer.tiketeer.domain.file.strategy.FileUploadStrategy
import com.tiketeer.tiketeer.domain.file.usecase.LocalFileRetrieveUseCase
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
@RequestMapping("/file")
class FileController @Autowired constructor(
    private val localFileRetrieveUseCase: LocalFileRetrieveUseCase,
    private val uploadFileUseCase: UploadFileUseCase
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFile(
        @RequestPart("file") fileMono: Mono<FilePart>,
        @RequestPart("dto") dtoMono: Mono<UploadFileRequestDto>
    ): Mono<ResponseEntity<String>> {
        return fileMono.zipWith(dtoMono) { file, dto ->
            UploadFileCommandDto(
                fileName = dto.fileName,
                file = file
            )
        }.flatMap { commandDto ->
            uploadFileUseCase.uploadFile(commandDto)
        }.map { fileUrl ->
            ResponseEntity.ok(fileUrl)
        }
    }


    @GetMapping("/local/{fileName}")
    fun getLocalFile(@PathVariable fileName: String): Mono<ResponseEntity<Flux<DataBuffer>>> {
        val fileTypeMap = MimetypesFileTypeMap()
        val mimeType = fileTypeMap.getContentType(fileName)

        return Mono.just(
            ResponseEntity.ok()
                .contentType(MediaType.valueOf(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$fileName\"")
                .body(localFileRetrieveUseCase.retrieveFile(fileName))
        )
    }


}
