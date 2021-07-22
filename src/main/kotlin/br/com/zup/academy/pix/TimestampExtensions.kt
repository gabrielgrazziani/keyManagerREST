package br.com.zup.academy.pix

import com.google.protobuf.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun Timestamp.paraLocalDateTime(): LocalDateTime {
    val instant = Instant.ofEpochSecond(seconds, nanos.toLong())
    return LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
}

fun LocalDateTime.paraTimestamp(): Timestamp {
    val instant = this.atZone(ZoneId.of("UTC")).toInstant()
    return Timestamp.newBuilder()
        .setSeconds(instant.epochSecond)
        .setNanos(instant.nano)
        .build()
}