package com.tiketeer.tiketeer.domain.file.strategy

import com.tiketeer.tiketeer.StorageFile
import com.tiketeer.tiketeer.domain.file.strategy.FileUploadStrategy
import com.tiketeer.tiketeer.domain.file.util.toByteArray
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.core.async.AsyncRequestBody
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux
import java.util.UUID

class S3FileUploadStrategy(
    private val s3AsyncClient: S3AsyncClient,

    @Value("\${cloud.aws.s3.bucket}")
    private val bucketName: String,

    @Value("\${cloud.aws.region.static}")
    private val region: String
): FileUploadStrategy {
    override fun uploadFile(file: StorageFile): Mono<String> {
        val objectKey = UUID.randomUUID().toString() + "_" + file.fileName
        val fileUrl = generateFileUrl(objectKey)
        val filePart = file.file


        getPutObjectRequest(objectKey, filePart)
                .zipWith(filePart.content().toByteArray())
                .flatMap { tuple ->
                    val request = tuple.t1
                    val bytes = tuple.t2
                    Mono.fromFuture(
                            s3AsyncClient.putObject(request, AsyncRequestBody.fromBytes(bytes))
                    )
                }
                .map { generateFileUrl(objectKey) }
                .doOnError { error -> println("Error uploading file: ${error.message}") }
            .subscribe()


        return Mono.just(fileUrl)
    }

    private fun getPutObjectRequest(key: String, filePart: FilePart): Mono<PutObjectRequest> {
        return DataBufferUtils
                .join(filePart.content())
                .map {
                    PutObjectRequest.builder()
                            .contentType(filePart.headers().contentType!!.toString())
                            .contentLength(it.readableByteCount().toLong())
                            .key(key)
                            .bucket(bucketName)
                            .build()
                }
    }

    override fun uploadFiles(files: List<StorageFile>): Flux<String> {
        return Flux.fromIterable(files).flatMap { uploadFile(it) }
    }

    private fun generateFileUrl(objectKey: String): String {
        return "https://$bucketName.s3.$region.amazonaws.com/$objectKey"
    }
}