package com.tiketeer.tiketeer.domain.file.usecase

import com.tiketeer.tiketeer.domain.file.dto.UploadFileCommandDto
import com.tiketeer.tiketeer.domain.file.strategy.FileStorageStrategy
import com.tiketeer.tiketeer.domain.file.strategy.LocalFileStorageStrategy
import com.tiketeer.tiketeer.domain.sign.SignService
import com.tiketeer.tiketeer.strategy.MockFilePart
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import reactor.test.StepVerifier
import java.nio.file.Path

@SpringBootTest
class UploadFileUseCaseTest {

    @TempDir
    lateinit var tempDir: Path

    @Mock
    private lateinit var signService: SignService

    private lateinit var uploadFileUseCase: UploadFileUseCase

    private lateinit var uploadFileCommandDto: UploadFileCommandDto
    private lateinit var fileStorageStrategy: FileStorageStrategy


    @BeforeEach
    fun setUp() {

        fileStorageStrategy = LocalFileStorageStrategy(tempDir.toString())
        uploadFileUseCase = UploadFileUseCase(fileStorageStrategy, signService)

        val image = MockMultipartFile(
            "file", "image.png", "image/png", "Image Content".toByteArray()
        )

        val filePart = MockFilePart(image)
        uploadFileCommandDto = UploadFileCommandDto(
            file = filePart,
            fileName = "test-file.txt",
            signature = "valid-signature"
        )

    }

    @Test
    fun `유효한 Signature - 파일 업로드 시도 - 성공`() {
        // given
        `when`(signService.verify(anyString(), anyString())).thenReturn(true)

        // when
        val result = uploadFileUseCase.uploadFile(uploadFileCommandDto)

        // then
        StepVerifier.create(result)
            .verifyComplete()
    }

    @Test
    fun `잘못된 Signature - 파일 업로드 시도 - 실패`() {
        // given
        `when`(signService.verify(anyString(), anyString())).thenReturn(false)

        // when
        val result = uploadFileUseCase.uploadFile(uploadFileCommandDto)

        // then
        StepVerifier.create(result)
            .expectError(RuntimeException::class.java)
            .verify()

    }
}