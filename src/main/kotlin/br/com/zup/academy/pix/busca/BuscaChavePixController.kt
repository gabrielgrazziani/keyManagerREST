package br.com.zup.academy.pix.busca

import br.com.zup.academy.BuscaChavePixPorIdPixRequest
import br.com.zup.academy.BuscaChavePixResponse
import br.com.zup.academy.KeymanagerBuscaGrpcServiceGrpc
import br.com.zup.academy.error.ErrorHandlerGrpc
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
class BuscaChavePixController(
    val grpc: KeymanagerBuscaGrpcServiceGrpc.KeymanagerBuscaGrpcServiceBlockingStub
) {

    @Get("{idPix}")
    fun busca(@PathVariable idTitular: UUID, @PathVariable idPix: UUID): HttpResponse<DetelhesChavePixResponse>{
        val request = BuscaChavePixPorIdPixRequest.newBuilder()
            .setIdPix(idPix.toString())
            .setIdTitular(idTitular.toString())
            .build()
        val response = grpc.buscaPorIdPix(request)

        return HttpResponse.ok(response.paraDetelhesChavePixResponse())
    }
}

fun  BuscaChavePixResponse.paraDetelhesChavePixResponse(): DetelhesChavePixResponse{
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

fun Timestamp.paraLocalDateTime(): LocalDateTime{
    val instant = Instant.ofEpochSecond(seconds, nanos.toLong())
    return LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
}

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