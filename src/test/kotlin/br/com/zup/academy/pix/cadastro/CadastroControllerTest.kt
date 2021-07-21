package br.com.zup.academy.pix.cadastro

import br.com.zup.academy.*
import br.com.zup.academy.config.GrpcClientFactory
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
internal class CadastroControllerTest{

    @field:Inject
    lateinit var keyManagerGrpc: KeyManagerGRPCServiceGrpc.KeyManagerGRPCServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    companion object{
        val ID_TITULAR = UUID.randomUUID()
    }

    @Test
    internal fun `deve cadastrar com sucesso`() {
        val ID_PIX = UUID.randomUUID().toString()
        val ID_TITULAR = UUID.randomUUID()
        val novaChavePix = novaChave()
        val chavePixRequest = novaChavePix.paraChavePixRequest(ID_TITULAR)

        Mockito.`when`(keyManagerGrpc.cadastro(chavePixRequest))
                .thenReturn(chavePixResponse(ID_PIX))

        val request = HttpRequest.POST("/api/v1/clientes/${ID_TITULAR}/pix", novaChavePix)
        val response = client.toBlocking().exchange(request,NovaChavePix::class.java)

        assertEquals(HttpStatus.CREATED,response.status)
        assertTrue(response.headers.contains("Location"))
        assertTrue(response.header("Location")!!.contains(ID_PIX))
        verify(keyManagerGrpc, times(1)).cadastro(chavePixRequest)
    }

    fun novaChave(
        tipoChave: TipoChaveRequest = TipoChaveRequest.EMAIL,
        tipoConta: TipoContaRequest = TipoContaRequest.CONTA_CORRENTE,
        valorChave: String = "email@teste.com"
    ): NovaChavePix {
        return NovaChavePix(
            tipoChave = tipoChave,
            tipoConta = tipoConta,
            valorChave = valorChave
        )
    }

    fun chavePixResponse(idPix: String) = ChavePixResponse
        .newBuilder()
        .setId(idPix)
        .build()

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class CadastroControllerTestFactory {
        @Singleton
        fun keyManagerGrpc() = Mockito.mock(KeyManagerGRPCServiceGrpc.KeyManagerGRPCServiceBlockingStub::class.java)
    }
}