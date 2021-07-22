package br.com.zup.academy.pix.busca

import br.com.zup.academy.*
import br.com.zup.academy.ChavePixResponse
import br.com.zup.academy.KeymanagerBuscaGrpcServiceGrpc.*
import br.com.zup.academy.config.GrpcClientFactory
import com.google.protobuf.Timestamp
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
import org.mockito.Mockito.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class BuscaChavePixControllerTest{

    @field:Inject
    lateinit var keyManagerGrpc: KeymanagerBuscaGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    internal fun `deve buscar a chave`() {
        val ID_PIX = UUID.randomUUID().toString()
        val ID_TITULAR = UUID.randomUUID().toString()

        val buscaChavePix = BuscaChavePixPorIdPixRequest
            .newBuilder()
            .setIdPix(ID_PIX)
            .setIdTitular(ID_TITULAR)
            .build()

        `when`(keyManagerGrpc.buscaPorIdPix(buscaChavePix))
            .thenReturn(buscaChavePixResponse(ID_PIX,ID_TITULAR))

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$ID_TITULAR/pix/$ID_PIX")
        val response = client.toBlocking().exchange(request,DetelhesChavePixResponse::class.java)

        assertEquals(HttpStatus.OK,response.status)
        assertEquals(ID_PIX,response.body().idPix)
        assertEquals(ID_TITULAR,response.body().idTitular)
        verify(keyManagerGrpc, times(1)).buscaPorIdPix(buscaChavePix)
    }

    private fun buscaChavePixResponse(idPix: String, idTitular: String): BuscaChavePixResponse {
        return BuscaChavePixResponse.newBuilder()
            .setIdPix(idPix)
            .setIdTitular(idTitular)
            .setChave(BuscaChavePixResponse.ChavePix.newBuilder()
                .setTipo(TipoChave.EMAIL)
                .setChave("teste@email.com")
                .setCriadaEm(LocalDateTime.now().paraTimestamp())
                .setConta(BuscaChavePixResponse.ContaInfo.newBuilder()
                    .setTipo(TipoConta.CONTA_CORRENTE)
                    .setInstituicao("ITAÚ UNIBANCO S.A.")
                    .setAgencia("0001")
                    .setCpfDoTitular("86135457004")
                    .setNumeroDaConta("123455")
                    .setNomeDoTitular("Gabrie Grazziani")
                    .build()))
            .build()
    }

    fun LocalDateTime.paraTimestamp(): Timestamp {
        val instant = this.atZone(ZoneId.of("UTC")).toInstant()
        return Timestamp.newBuilder()
            .setSeconds(instant.epochSecond)
            .setNanos(instant.nano)
            .build()
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class DeletarChavePixControllerTestFactory {
        @Singleton
        fun keyManagerGrpc() = mock(KeymanagerBuscaGrpcServiceBlockingStub::class.java)
    }
}