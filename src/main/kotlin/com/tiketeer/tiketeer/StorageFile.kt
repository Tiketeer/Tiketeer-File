package com.tiketeer.tiketeer

import org.springframework.http.codec.multipart.FilePart


class StorageFile(val file: FilePart, val fileName: String) {
}