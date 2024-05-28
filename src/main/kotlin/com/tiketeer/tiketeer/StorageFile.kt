package com.tiketeer.tiketeer

import org.springframework.http.codec.multipart.FilePart
import java.util.*


class StorageFile(val file: FilePart) {
    val fileName = "${UUID.randomUUID()}_${file.filename()}"
}