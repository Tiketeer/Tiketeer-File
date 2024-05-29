package com.tiketeer.tiketeer.domain.file.controller

import com.tiketeer.tiketeer.StorageFile
import com.tiketeer.tiketeer.domain.file.strategy.FileStorageStrategy
import com.tiketeer.tiketeer.domain.file.strategy.LocalFileStorageStrategy
import com.tiketeer.tiketeer.strategy.MockFilePart
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileControllerTest {

    companion object {
        @TempDir
        lateinit var tempDir: Path
    }

    @Autowired
    lateinit var webTestClient: WebTestClient


    @TestConfiguration
    class TestConfig() {
        @Bean
        fun fileStorageStrategy(): FileStorageStrategy {
            return LocalFileStorageStrategy(tempDir.toString())
        }
    }

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
            .expectStatus().isOk()

    }

    @Test
    fun getFile() {

        val image = MockMultipartFile(
            "file", "image.png", "image/png", "Image Content".toByteArray()
        )

        val filePart = MockFilePart(image)
        val storageFile = StorageFile(filePart)
        val fileName = storageFile.fileName
        val filePath = tempDir.resolve(fileName)
        Files.write(filePath, "Image Content".toByteArray(), StandardOpenOption.CREATE)


        webTestClient.get()
            .uri("/files/$fileName")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.IMAGE_PNG)
            .expectBody()
            .consumeWith { response ->
                val body = response.responseBody
                assertThat(String(body!!)).isEqualTo("Image Content")
            }


    }


}