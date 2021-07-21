package br.com.zup.academy.pix.deletar

import br.com.zup.academy.KeyManagerGRPCServiceGrpc
import br.com.zup.academy.KeymanagerRemoveGrpcServiceGrpc
import br.com.zup.academy.RemoveChavePixRequest
import br.com.zup.academy.config.GrpcClientFactory
import br.com.zup.academy.pix.cadastro.NovaChavePix
import com.google.protobuf.Empty
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class DeletarChavePixControllerTest{

    @field:Inject
    lateinit var keyManagerGrpc: KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    internal fun `deve deletar a chave`() {
        val ID_PIX = UUID.randomUUID().toString()
        val ID_TITULAR = UUID.randomUUID().toString()

        val removeChavePix = RemoveChavePixRequest
            .newBuilder()
            .setIdPix(ID_PIX)
            .setIdTitular(ID_TITULAR)
            .build()

        val request = HttpRequest.DELETE("/api/v1/clientes/$ID_TITULAR/pix/$ID_PIX",null)
        val response = client.toBlocking().exchange(request,Any::class.java)

        assertEquals(HttpStatus.NO_CONTENT,response.status)
        verify(keyManagerGrpc, times(1)).remove(removeChavePix)
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class DeletarChavePixControllerTestFactory {
        @Singleton
        fun keyManagerGrpc() = Mockito.mock(KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceBlockingStub::class.java)
    }
}