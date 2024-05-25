package com.tiketeer.tiketeer.domain.file.controller

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import java.nio.file.Path

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileControllerTest {

    @TempDir
    lateinit var tempDir: Path

    @Autowired
    lateinit var webTestClient: WebTestClient


    @Test
    fun uploadFile() {

        val image = MockMultipartFile(
            "file", "image.png", "image/png", "Image Content".toByteArray()
        )

        //when - then
        val multipart = MultipartBodyBuilder().apply {
            part("file", image.resource)
        }.build()

        webTestClient.post()
            .uri("/files")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(multipart))
            .exchange()
            .expectStatus().isOk

    }

    @Test
    fun getFile() {
 
        val image = MockMultipartFile(
            "file", "image.png", "image/png", "Image Content".toByteArray()
        )
    }
}