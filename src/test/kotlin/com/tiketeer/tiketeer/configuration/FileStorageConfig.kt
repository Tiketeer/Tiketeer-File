package com.tiketeer.tiketeer.configuration

import com.tiketeer.tiketeer.domain.file.strategy.FileStorageStrategy
import com.tiketeer.tiketeer.domain.file.strategy.LocalFileStorageStrategy
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Configuration
class FileStorageConfig {

    @Value("\${custom.policy.local-storage-path}")
    private lateinit var storagePath: String

    @Bean
    @Profile("test")
    fun fileStorageStrategy(): FileStorageStrategy {
        return LocalFileStorageStrategy(storagePath)
    }
}