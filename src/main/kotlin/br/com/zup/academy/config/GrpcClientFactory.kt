package br.com.zup.academy.config

import br.com.zup.academy.KeyManagerGRPCServiceGrpc
import br.com.zup.academy.KeymanagerBuscaGrpcServiceGrpc
import br.com.zup.academy.KeymanagerRemoveGrpcServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory(@GrpcChannel("key-manager-grpc") val channel: ManagedChannel) {

    @Singleton
    fun cadastro() = KeyManagerGRPCServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun deleta() = KeymanagerRemoveGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun busca() = KeymanagerBuscaGrpcServiceGrpc.newBlockingStub(channel)

}