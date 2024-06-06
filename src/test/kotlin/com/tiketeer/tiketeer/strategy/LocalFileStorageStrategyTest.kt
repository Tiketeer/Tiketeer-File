package com.tiketeer.tiketeer.strategy

import com.tiketeer.tiketeer.StorageFile
import com.tiketeer.tiketeer.domain.file.strategy.LocalFileStorageStrategy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.mock.web.MockMultipartFile
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class LocalFileStorageStrategyTest {

    @field: TempDir
    lateinit var tempDir: Path

    private lateinit var storageStrategy: LocalFileStorageStrategy

    @BeforeEach
    fun setUp() {
        storageStrategy = LocalFileStorageStrategy(tempDir.toString())
    }

    @Test
    fun `임시파일 다수 - 로컬에 저장 - 파일 존재 확인`() {
        //given
        val mockFile: MockMultipartFile = MockMultipartFile(
            "file",
            "testfile.png",
            "image/png",
            "test".toByteArray()
        )

        // When
        val filePart = MockFilePart(mockFile)
        val filePathFlux: Flux<String> = storageStrategy.uploadFiles(listOf(StorageFile(filePart, "testfile.png")))

        // Then
        StepVerifier.create(filePathFlux.collectList())
            .assertNext { filePathList ->
                assertThat(filePathList).hasSize(1)
                val savedFileName = Paths.get(filePathList.first()).fileName
                val savedFilePath = tempDir.resolve(savedFileName)
                assertThat(Files.exists(savedFilePath)).isTrue()
                assertThat(String(Files.readAllBytes(savedFilePath))).isEqualTo("test")
            }
            .verifyComplete()

    }

    @Test
    fun `임시파일 생성 - 파일 요청 - 파일 전달`() {
        //given
        val mockFile = MockMultipartFile(
            "file",
            "testfile.png",
            "image/png",
            "test".toByteArray()
        )

        //when
        mockFile.transferTo(tempDir.resolve(mockFile.originalFilename).toFile())
        val retrievedFileFlux: Flux<DataBuffer> = storageStrategy.retrieveFile(mockFile.originalFilename!!)


        // Then
        val byteArrayMono = DataBufferUtils.join(retrievedFileFlux)
            .map { dataBuffer ->
                val byteArray = ByteArray(dataBuffer.readableByteCount())
                dataBuffer.read(byteArray)
                DataBufferUtils.release(dataBuffer)
                byteArray
            }

        StepVerifier.create(byteArrayMono)
            .assertNext { retrievedFileData ->
                assertThat(retrievedFileData).isEqualTo(mockFile.bytes)
            }
            .verifyComplete()
    }

}