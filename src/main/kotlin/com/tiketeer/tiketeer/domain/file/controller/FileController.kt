package com.tiketeer.Tiketeer.infra.storage

import com.tiketeer.tiketeer.StorageFile
import com.tiketeer.tiketeer.strategy.FileStorageStrategy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.activation.MimetypesFileTypeMap

@RestController
@RequestMapping("/files")
class FileController @Autowired constructor(
    private val fileStorageStrategy: FileStorageStrategy
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFile(@RequestPart("file") file: Mono<FilePart>): Mono<ResponseEntity<Void>> {
        return file.flatMap { filePart ->
            fileStorageStrategy.uploadFile(StorageFile(filePart))
        }.then(Mono.just(ResponseEntity.ok().build()))
    }

    @GetMapping("/{fileId}")
    fun getFile(@PathVariable fileId: String): Mono<ResponseEntity<ByteArray>> {
        val fileTypeMap = MimetypesFileTypeMap()
        val mimeType = fileTypeMap.getContentType(fileId)

        return fileStorageStrategy.retrieveFile(fileId)
            .map { storageFile ->
                ResponseEntity.ok()
                    .contentType(MediaType.valueOf(mimeType))
                    .body(storageFile)
            }
    }
}
