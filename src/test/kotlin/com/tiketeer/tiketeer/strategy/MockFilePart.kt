package com.tiketeer.tiketeer.strategy

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.codec.multipart.FilePart
import org.springframework.mock.web.MockMultipartFile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.file.Path

class MockFilePart(private val mockMultipartFile: MockMultipartFile) : FilePart {

    override fun name(): String = mockMultipartFile.name

    override fun filename(): String = mockMultipartFile.originalFilename ?: "unknown"

    override fun headers(): HttpHeaders = HttpHeaders().apply {
        this.add(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"${mockMultipartFile.name}\"; filename=\"${mockMultipartFile.originalFilename}\"")
        this.add(HttpHeaders.CONTENT_TYPE, mockMultipartFile.contentType ?: "application/octet-stream")
    }

    override fun content(): Flux<DataBuffer> {
        val dataBuffer = DefaultDataBufferFactory().wrap(mockMultipartFile.bytes)
        return Flux.just(dataBuffer)
    }

    override fun transferTo(dest: Path): Mono<Void> {
        return Mono.fromCallable {
            mockMultipartFile.transferTo(dest.toFile())
            null
        }
    }
}