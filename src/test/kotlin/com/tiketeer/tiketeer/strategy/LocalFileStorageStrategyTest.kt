package com.tiketeer.tiketeer.strategy

import com.tiketeer.tiketeer.StorageFile
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.mock.web.MockMultipartFile
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
                "test".toByteArray())

        //when
        val file: StorageFile = StorageFile(
                fileName = mockFile.name,
                fileData = mockFile.bytes
        )

        val filePath: List<String> = storageStrategy.uploadFiles(listOf(file))

        //then
        val savedFileName = Paths.get(filePath.first())
        val savedFilePath = tempDir.resolve(savedFileName)
        assertThat(Files.exists(savedFilePath)).isTrue()
        assertThat(String(Files.readAllBytes(savedFilePath))).isEqualTo("test")

    }

    @Test
    fun `임시파일 생성 - 파일 요청 - 파일 전달`() {
        //given
        val mockFile: MockMultipartFile = MockMultipartFile(
            "file",
            "testfile.png",
            "image/png",
            "test".toByteArray())

        //when
        mockFile.transferTo(tempDir.resolve(mockFile.name).toFile())
        val retrievedFile = storageStrategy.retrieveFile(mockFile.name)

        assertThat(retrievedFile.fileData).isEqualTo(mockFile.bytes)


    }

}