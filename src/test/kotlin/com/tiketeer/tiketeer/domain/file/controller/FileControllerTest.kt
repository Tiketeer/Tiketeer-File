package com.tiketeer.tiketeer.domain.file.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.tiketeer.tiketeer.StorageFile
import com.tiketeer.tiketeer.domain.file.dto.UploadFileRequestDto
import com.tiketeer.tiketeer.domain.file.strategy.FileStorageStrategy
import com.tiketeer.tiketeer.domain.file.strategy.LocalFileStorageStrategy
import com.tiketeer.tiketeer.strategy.MockFilePart
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileControllerTest {

    companion object {
        @TempDir
        lateinit var tempDir: Path
    }

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Value("\${jwt.secret-key}")
    private lateinit var secretKey: String


    @TestConfiguration
    class TestConfig() {
        @Bean
        fun fileStorageStrategy(): FileStorageStrategy {
            return LocalFileStorageStrategy(tempDir.toString())
        }
    }

    @Test
    fun `파일 업로드 시도 - 성공`() {

        //given
        val sha256Mac: Mac = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(), "HmacSHA256")
        sha256Mac.init(secretKeySpec)

        val fileName = "image.png"
        val signature = sha256Mac.doFinal(fileName.toByteArray())
        val base64Signature = Base64.getUrlEncoder().withoutPadding().encodeToString(signature);

        val image = MockMultipartFile(
            "file", fileName, "image/png", "Image Content".toByteArray()
        )

        val uploadFileRequestDto = UploadFileRequestDto(fileName, base64Signature)
        val dtoJson = objectMapper.writeValueAsString(uploadFileRequestDto)

        val multipart = MultipartBodyBuilder().apply {
            part("file", image.resource)
            part("dto", dtoJson, MediaType.APPLICATION_JSON)
        }.build()

        //when - then
        webTestClient.post()
            .uri("/file")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(multipart))
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `파일 이름 - 조회 시도 - 성공`() {
        //given
        val image = MockMultipartFile(
            "file", "image.png", "image/png", "Image Content".toByteArray()
        )

        val filePart = MockFilePart(image)
        val storageFile = StorageFile(filePart, "image.png")
        val fileName = storageFile.fileName
        val filePath = tempDir.resolve(fileName)
        //when
        Files.write(filePath, "Image Content".toByteArray(), StandardOpenOption.CREATE)

        //then
        webTestClient.get()
            .uri("/file/$fileName")
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