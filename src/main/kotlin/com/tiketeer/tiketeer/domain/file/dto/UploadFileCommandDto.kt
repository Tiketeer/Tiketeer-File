package com.tiketeer.tiketeer.domain.file.dto

import org.springframework.http.codec.multipart.FilePart

data class UploadFileCommandDto(
    val fileName: String,
    val signature: String,
    val file: FilePart
)