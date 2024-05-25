package com.tiketeer.tiketeer.configuration

import com.tiketeer.tiketeer.constant.StorageEnum
import com.tiketeer.tiketeer.strategy.FileStorageStrategy
import com.tiketeer.tiketeer.strategy.LocalFileStorageStrategy
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test")
class FileStorageConfig {
    @Value("\${custom.policy.local-storage-path}")
    lateinit var localStoragePath: String

    @Value("\${custom.policy.storage}")
    lateinit var storageEnum: StorageEnum


    @Bean
    fun fileStorageStrategy(): LocalFileStorageStrategy? {
        return when (storageEnum) {
            StorageEnum.LOCAL -> LocalFileStorageStrategy(localStoragePath)
            else -> null
        }
    }
}