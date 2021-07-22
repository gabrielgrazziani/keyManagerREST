package br.com.zup.academy.error

import com.google.rpc.BadRequest
import com.google.rpc.Code
import io.grpc.BindableService
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito

internal class ErrorHandlerGrpcInterceptorTest{

    val interceptor = ErrorHandlerGrpcInterceptor()

    @Test
    internal fun `deve dar um 404 quando o grpc der NOT_FOUND`() {
        val mensagem = "não encontrado"
        val notFoundException = StatusRuntimeException(Status.NOT_FOUND
            .withDescription(mensagem))

        val context = Mockito.mock(MethodInvocationContext::class.java)
                as MethodInvocationContext<BindableService, Any?>

        Mockito.`when`(context.proceed())
            .thenThrow(notFoundException)

        val resposta = interceptor.intercept(context) as HttpResponse<ErrorResponse>

        assertEquals(HttpStatus.NOT_FOUND, resposta.status)
        assertNotNull(resposta.body())
        assertEquals(mensagem, resposta.body().message)
    }

    @Test
    internal fun `deve dar um 422 quando o grpc der ALREADY_EXISTS`() {
        val mensagem = "registro já existe"
        val notFoundException = StatusRuntimeException(Status.ALREADY_EXISTS
            .withDescription(mensagem))

        val context = Mockito.mock(MethodInvocationContext::class.java)
                as MethodInvocationContext<BindableService, Any?>

        Mockito.`when`(context.proceed())
            .thenThrow(notFoundException)

        val resposta = interceptor.intercept(context) as HttpResponse<ErrorResponse>

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, resposta.status)
        assertNotNull(resposta.body())
        assertEquals(mensagem, resposta.body().message)
    }

    @Test
    internal fun `deve dar um 400 quando o grpc der INVALID_ARGUMENT`() {
        val mensagem = "dados incoretos"

        val details = BadRequest.newBuilder()
            .addFieldViolations(
                BadRequest.FieldViolation.newBuilder()
                    .setField("campo-x")
                    .setDescription("esta vazio")
                    .build()
            )
            .build()

        val statusProto = com.google.rpc.Status.newBuilder()
            .setCode(Code.INVALID_ARGUMENT_VALUE)
            .setMessage(mensagem)
            .addDetails(com.google.protobuf.Any.pack(details))
            .build()

        val notFoundException = StatusProto
            .toStatusRuntimeException(statusProto)

        val context = Mockito.mock(MethodInvocationContext::class.java)
                as MethodInvocationContext<BindableService, Any?>

        Mockito.`when`(context.proceed())
            .thenThrow(notFoundException)

        val resposta = interceptor.intercept(context) as HttpResponse<ErrorResponse>

        assertEquals(HttpStatus.BAD_REQUEST, resposta.status)
        assertNotNull(resposta.body())
        val body = resposta.body()
        assertEquals(mensagem, resposta.body().message)

        assertEquals(1,body.items.size)
        assertEquals("campo-x",body.items[0].fild)
        assertEquals("esta vazio",body.items[0].description)
    }

    @Test
    internal fun `deve dar um 500 quando o grpc der um erro que não foi possiver tratar`() {
        val mensagem = "erro interno"
        val notFoundException = StatusRuntimeException(Status.INTERNAL
            .withDescription(mensagem))

        val context = Mockito.mock(MethodInvocationContext::class.java)
                as MethodInvocationContext<BindableService, Any?>

        Mockito.`when`(context.proceed())
            .thenThrow(notFoundException)

        val resposta = interceptor.intercept(context) as HttpResponse<ErrorResponse>

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.status)
        assertNotNull(resposta.body())
        assertEquals(mensagem, resposta.body().message)
    }


}