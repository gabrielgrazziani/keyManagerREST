package br.com.zup.academy.pix.carrega

import br.com.zup.academy.*
import br.com.zup.academy.KeymanagerBuscaGrpcServiceGrpc.*
import br.com.zup.academy.KeymanagerListarGrpcServiceGrpc.*
import br.com.zup.academy.config.GrpcClientFactory
import br.com.zup.academy.pix.paraTimestamp
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class CarregaChavePixControllerTest{

    @field:Inject
    lateinit var keyManagerBuscaGrpc: KeymanagerBuscaGrpcServiceBlockingStub

    @field:Inject
    lateinit var keyManagerListaGrpc: KeymanagerListarGrpcServiceBlockingStub

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

        `when`(keyManagerBuscaGrpc.buscaPorIdPix(buscaChavePix))
            .thenReturn(buscaChavePixResponse(ID_PIX,ID_TITULAR))

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$ID_TITULAR/pix/$ID_PIX")
        val response = client.toBlocking().exchange(request, DetelhesChavePixResponse::class.java)

        assertEquals(HttpStatus.OK,response.status)
        assertEquals(ID_PIX,response.body().idPix)
        assertEquals(ID_TITULAR,response.body().idTitular)
        verify(keyManagerBuscaGrpc, times(1)).buscaPorIdPix(buscaChavePix)
    }

    @Test
    internal fun `deve lista as chaves`() {
        val ID_TITULAR = UUID.randomUUID().toString()

        val listaChavePix = ListaChavesRequest
            .newBuilder()
            .setIdTitular(ID_TITULAR)
            .build()

        `when`(keyManagerListaGrpc.listar(listaChavePix))
            .thenReturn(listaChavePixResponse(ID_TITULAR))

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$ID_TITULAR/pix")
        val response = client.toBlocking().exchange(request, Argument.listOf(ChavePixItemListaResponse::class.java))

        assertEquals(HttpStatus.OK,response.status)
        assertEquals(2,response.body().size)
        assertEquals(ID_TITULAR,response.body()[0].idTitular)
        assertEquals(ID_TITULAR,response.body()[1].idTitular)
        verify(keyManagerListaGrpc, times(1)).listar(listaChavePix)
    }

    private fun listaChavePixResponse(idTitular: String): ListaChavesResponse {
        val lista = mutableListOf<ListaChavesResponse.ChavesResponse>()

        lista.add(ListaChavesResponse.ChavesResponse.newBuilder()
            .setIdPix(UUID.randomUUID().toString())
            .setIdTitular(idTitular)
            .setTipoChave(TipoChave.CHAVE_ALEATORIA)
            .setValorChave(UUID.randomUUID().toString())
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .setCriadoEm(LocalDateTime.now().paraTimestamp())
            .build())

        lista.add(ListaChavesResponse.ChavesResponse.newBuilder()
            .setIdPix(UUID.randomUUID().toString())
            .setIdTitular(idTitular)
            .setTipoChave(TipoChave.EMAIL)
            .setValorChave("teste@email.com")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .setCriadoEm(LocalDateTime.now().paraTimestamp())
            .build())

        return ListaChavesResponse.newBuilder()
            .addAllChaves(lista)
            .build()
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

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class DeletarChavePixControllerTestFactory {
        @Singleton
        fun keyManagerBuscaGrpc() = mock(KeymanagerBuscaGrpcServiceBlockingStub::class.java)

        @Singleton
        fun keyManagerListaGrpc() = mock(KeymanagerListarGrpcServiceBlockingStub::class.java)
    }
}