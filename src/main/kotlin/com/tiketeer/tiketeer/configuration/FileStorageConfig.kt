package com.tiketeer.tiketeer.configuration

import com.tiketeer.tiketeer.constant.StorageEnum
import com.tiketeer.tiketeer.domain.file.strategy.FileUploadStrategy
import com.tiketeer.tiketeer.domain.file.util.LocalFileSystemUtils
import com.tiketeer.tiketeer.domain.file.strategy.LocalFileUploadStrategy
import com.tiketeer.tiketeer.domain.file.strategy.S3FileUploadStrategy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.services.s3.S3AsyncClient

@Configuration
@Profile("!test")
class FileStorageConfig(
    @Autowired private val localFileSystemUtils: LocalFileSystemUtils,

    @Autowired private val s3AsyncClient: S3AsyncClient,
) {

    @Value("\${custom.policy.storage}")
    lateinit var storageEnum: StorageEnum

    @Value("\${cloud.aws.s3.bucket}")
    lateinit var bucketName: String

    @Value("\${cloud.aws.region.static}")
    lateinit var region: String

    @Bean
    fun fileUploadStrategy(): FileUploadStrategy? {

        return when (storageEnum) {
            StorageEnum.LOCAL -> LocalFileUploadStrategy(localFileSystemUtils)
            StorageEnum.S3 -> S3FileUploadStrategy(s3AsyncClient ,bucketName, region)
            else -> throw RuntimeException("Invalid storage type")
        }
    }
}