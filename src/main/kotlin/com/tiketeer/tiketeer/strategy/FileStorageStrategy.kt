package com.tiketeer.tiketeer.strategy

import com.tiketeer.tiketeer.StorageFile


interface FileStorageStrategy {
    fun uploadFiles(files: List<StorageFile>): List<String>

    fun uploadFile(file: StorageFile): String

    fun retrieveFile(fileId: String): StorageFile
}
