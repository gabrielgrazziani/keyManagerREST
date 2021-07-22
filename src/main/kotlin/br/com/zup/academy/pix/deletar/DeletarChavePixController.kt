package br.com.zup.academy.pix.deletar

import br.com.zup.academy.KeymanagerRemoveGrpcServiceGrpc
import br.com.zup.academy.RemoveChavePixRequest
import br.com.zup.academy.error.ErrorHandlerGrpc
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Singleton
import javax.validation.constraints.Size

@Controller("api/v1/clientes/{idTitular}/pix")
@ErrorHandlerGrpc
class DeletarChavePixController(
    val grpc: KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceBlockingStub
) {

    @Delete("{idPix}")
    fun deletar(@PathVariable idTitular: UUID,@PathVariable idPix: UUID): HttpResponse<Any>{
        val removeChavePixRequest = RemoveChavePixRequest
                                            .newBuilder()
                                            .setIdPix(idPix.toString())
                                            .setIdTitular(idTitular.toString())
                                            .build()
        grpc.remove(removeChavePixRequest)
        return HttpResponse.noContent()
    }
}
