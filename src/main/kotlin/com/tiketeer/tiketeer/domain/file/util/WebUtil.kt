package com.tiketeer.tiketeer.domain.file.util

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun Flux<DataBuffer>.toByteArray(): Mono<ByteArray> =
        DataBufferUtils.join(this)
                .map {
                    ByteArray(it.readableByteCount())
                        .also(it::read)
                        .apply { DataBufferUtils.release(it) }
                }