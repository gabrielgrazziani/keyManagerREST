package br.com.zup.academy.pix.cadastro

import br.com.zup.academy.*
import br.com.zup.academy.pix.UUIDValido
import br.com.zup.academy.error.ErrorHandlerGrpc
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Controller("api/v1/clientes/{idTitular}/pix")
@Validated
@ErrorHandlerGrpc
class CadastroController(
    val keyManagerGrpc: KeyManagerGRPCServiceGrpc.KeyManagerGRPCServiceBlockingStub
) {

    @Post
    fun cadastrar(@PathVariable idTitular: UUID,@Valid @Body novaChavePix: NovaChavePix): HttpResponse<Any>{
        val chavePixRequest = novaChavePix.paraChavePixRequest(idTitular)
        val response = keyManagerGrpc.cadastro(chavePixRequest)

        return HttpResponse
                .created(location(idTitular,response.id))
    }

    fun location(idTitular: UUID,idPix: String) = HttpResponse
                    .uri("/api/v1/clientes/${idTitular}/pix/${idPix}")
}

@Introspected
data class NovaChavePix (
    @field:Size(max = 77)
    val valorChave: String?,
    @field:NotNull
    val tipoChave: TipoChaveRequest?,
    @field:NotNull
    val tipoConta: TipoContaRequest?
){
    fun paraChavePixRequest(idTitular: UUID): ChavePixRequest{
        return ChavePixRequest.newBuilder()
            .setChave(valorChave)
            .setIdTitular(idTitular.toString())
            .setTipoChave(tipoChave?.tipoChaveGrpc ?: TipoChave.DESCONHECIDO_TIPO_CHAVE)
            .setTipoConta(tipoConta?.tipoContaGrpc ?: TipoConta.DESCONHECIDO_TIPO_CONTA)
            .build()
    }
}

enum class TipoChaveRequest(
    val tipoChaveGrpc: TipoChave
){
    CPF(TipoChave.CPF),
    TELEFONE_CELULAR(TipoChave.TELEFONE_CELULAR),
    EMAIL(TipoChave.EMAIL),
    CHAVE_ALEATORIA(TipoChave.CHAVE_ALEATORIA),
}

enum class TipoContaRequest(
    val tipoContaGrpc: TipoConta
){
    CONTA_CORRENTE(TipoConta.CONTA_CORRENTE),
    CONTA_POUPANCA(TipoConta.CONTA_POUPANCA);
}
