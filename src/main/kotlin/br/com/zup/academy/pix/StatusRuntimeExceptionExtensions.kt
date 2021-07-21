package br.com.zup.academy.pix

import com.google.rpc.BadRequest
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto

fun StatusRuntimeException.getViolacaons(): List<Pair<String, String>>? {
    val statusProto = StatusProto.fromThrowable(this)

    return statusProto
        ?.detailsList?.flatMap {
            it.unpack(BadRequest::class.java).fieldViolationsList
        }?.map {
            it.field to it.description
        }
}