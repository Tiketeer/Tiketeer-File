package com.tiketeer.tiketeer.domain.file.dto

data class UploadFileRequestDto(
    val fileName: String,
    val signature: String,
)