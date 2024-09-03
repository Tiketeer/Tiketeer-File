package com.tiketeer.tiketeer.configuration

import com.tiketeer.tiketeer.domain.file.strategy.FileUploadStrategy
import com.tiketeer.tiketeer.domain.file.strategy.LocalFileUploadStrategy
import com.tiketeer.tiketeer.domain.file.util.LocalFileSystemUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Configuration
class FileStorageConfig {

    @Value("\${custom.policy.local-storage-path}")
    private lateinit var storagePath: String

    @Autowired
    private lateinit var localFileSystemUtils: LocalFileSystemUtils

    @Bean
    @Profile("test")
    fun fileStorageStrategy(): FileUploadStrategy {
        return LocalFileUploadStrategy(localFileSystemUtils)
    }
}