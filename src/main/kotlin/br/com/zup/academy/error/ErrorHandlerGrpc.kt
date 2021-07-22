package br.com.zup.academy.error

import br.com.zup.academy.pix.getViolacaons
import io.grpc.BindableService
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.aop.Around
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.context.annotation.Type
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import kotlin.annotation.AnnotationTarget.*

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(CLASS, FILE, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
@Around
@Type(ErrorHandlerGrpcInterceptor::class)
annotation class ErrorHandlerGrpc

@Singleton
class ErrorHandlerGrpcInterceptor: MethodInterceptor<BindableService, Any?> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun intercept(context: MethodInvocationContext<BindableService, Any?>): Any? {
        try {
            return context.proceed()
        } catch (e: StatusRuntimeException) {

            logger.error("Handling the exception '${e.javaClass.name}' while processing the call: ${context.targetMethod}", e)
            Pair(HttpStatus.INTERNAL_SERVER_ERROR,Error(e.status.description))
            val (statusHttp,bodyError) = when (e.status.code) {
                Status.NOT_FOUND.code -> Pair(HttpStatus.NOT_FOUND,ErrorResponse(e.status.description ?: ""))
                Status.ALREADY_EXISTS.code -> Pair(HttpStatus.UNPROCESSABLE_ENTITY,ErrorResponse(e.status.description ?: ""))
                Status.INVALID_ARGUMENT.code -> {
                    val violacaons = e
                        .getViolacaons()
                        ?.map {
                            ItemError(fild = it.first,description = it.second)
                        }
                    Pair(HttpStatus.BAD_REQUEST, ErrorResponse(
                        message = e.status.description ?: "",
                        items = violacaons ?: listOf()
                    ))
                }
                else -> {
                    logger.error("Erro sem tratamento status: ${e.status.code} description: ${e.status.description}")
                    Pair(HttpStatus.INTERNAL_SERVER_ERROR,ErrorResponse(e.status.description ?: ""))
                }
            }

            return HttpResponse
                .status<ErrorResponse>(statusHttp)
                .body(bodyError)
        }
    }
}

data class ErrorResponse(
    val message:String = "",
    val items: List<ItemError> = listOf()
)

data class ItemError(
    val fild:String,
    val description:String
)
