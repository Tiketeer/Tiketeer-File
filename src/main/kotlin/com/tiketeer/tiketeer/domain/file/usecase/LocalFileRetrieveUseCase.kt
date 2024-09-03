package com.tiketeer.tiketeer.domain.file.usecase

import com.tiketeer.tiketeer.domain.file.util.LocalFileSystemUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers

@Service
class LocalFileRetrieveUseCase(
    @Autowired private val localFileSystemUtils: LocalFileSystemUtils
){

    private val logger = KotlinLogging.logger {}
    fun retrieveFile(fileId: String): Flux<DataBuffer> {
        val filePath = localFileSystemUtils.getPath(fileId)

        return DataBufferUtils.read(
                filePath,
                DefaultDataBufferFactory(),
                4096
        ).subscribeOn(Schedulers.boundedElastic())
            .doOnError {
                logger.error { "Failed to retrieve file $fileId at ${localFileSystemUtils.getAbsolutePath()}" }
                throw RuntimeException("File retrieval failed")
        }
    }

}