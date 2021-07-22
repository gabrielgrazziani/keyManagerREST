package br.com.zup.academy.pix.carrega

import br.com.zup.academy.*
import br.com.zup.academy.error.ErrorHandlerGrpc
import br.com.zup.academy.pix.paraLocalDateTime
import com.fasterxml.jackson.annotation.JsonFormat
import com.google.protobuf.Timestamp
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Controller("api/v1/clientes/{idTitular}/pix")
@ErrorHandlerGrpc
class CarregaChavePixController(
    val grpcBusca: KeymanagerBuscaGrpcServiceGrpc.KeymanagerBuscaGrpcServiceBlockingStub,
    val grpcLista: KeymanagerListarGrpcServiceGrpc.KeymanagerListarGrpcServiceBlockingStub
) {

    @Get("{idPix}")
    fun busca(@PathVariable idTitular: UUID, @PathVariable idPix: UUID): HttpResponse<DetelhesChavePixResponse>{
        val request = BuscaChavePixPorIdPixRequest.newBuilder()
            .setIdPix(idPix.toString())
            .setIdTitular(idTitular.toString())
            .build()
        val response = grpcBusca.buscaPorIdPix(request)

        return HttpResponse.ok(response.paraDetelhesChavePixResponse())
    }

    @Get
    fun lista(@PathVariable idTitular: UUID): HttpResponse<List<ChavePixItemListaResponse>>{
        val request = ListaChavesRequest.newBuilder()
            .setIdTitular(idTitular.toString())
            .build()
        val response = grpcLista.listar(request)
            .chavesList
            .map {
                it.paraChavePixItemListaResponse()
            };

        return HttpResponse.ok(response)
    }
}

fun  BuscaChavePixResponse.paraDetelhesChavePixResponse(): DetelhesChavePixResponse {
    return DetelhesChavePixResponse(
        idTitular = idTitular,
        idPix = idPix,
        chave = ChavePixResponse(
            tipoChave = chave.tipo.name,
            valorChave = chave.chave,
            criadaEm = chave.criadaEm.paraLocalDateTime(),
            conta = DetelhesContaResponse(
                instituicao = chave.conta.instituicao,
                tipoConta = chave.conta.tipo.name,
                nomeDoTitular = chave.conta.nomeDoTitular,
                cpfDoTitular = chave.conta.cpfDoTitular,
                agencia = chave.conta.agencia,
                numeroDaConta = chave.conta.numeroDaConta
            )
        )
    )
}

fun ListaChavesResponse.ChavesResponse.paraChavePixItemListaResponse(): ChavePixItemListaResponse {
    return ChavePixItemListaResponse(
        idPix = idPix,
        idTitular = idTitular,
        tipoChave = tipoChave.name,
        tipoConta = tipoConta.name,
        valorChave = valorChave,
        criadaEm = criadoEm.paraLocalDateTime()
    )
}

@Introspected
data class ChavePixItemListaResponse(
    val idTitular: String,
    val idPix: String,
    val tipoChave: String,
    val valorChave: String,
    val tipoConta: String,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss")
    val criadaEm: LocalDateTime,
)

data class DetelhesChavePixResponse(
   val idTitular: String,
   val idPix: String,
   val chave: ChavePixResponse
)

@Introspected
data class ChavePixResponse(
    val tipoChave: String,
    val valorChave: String,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss")
    val criadaEm: LocalDateTime,
    val conta: DetelhesContaResponse
)

data class DetelhesContaResponse(
    val instituicao: String,
    val tipoConta: String,
    val nomeDoTitular: String,
    val cpfDoTitular: String,
    val agencia: String,
    val numeroDaConta: String
)