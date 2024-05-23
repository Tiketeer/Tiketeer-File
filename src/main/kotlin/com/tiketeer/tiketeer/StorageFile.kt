package com.tiketeer.tiketeer

import java.util.*

class StorageFile constructor(fileName: String, val fileData: ByteArray) {
    val fileName: String = UUID.randomUUID().toString() + "_" + fileName
}
