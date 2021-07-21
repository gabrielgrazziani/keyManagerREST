package br.com.zup.academy.pix

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

internal class UUIDValidatorTest{

    @Test
    internal fun `deve validar eu UUID valido`() {
        assertTrue(UUIDValidator().isValid(UUID.randomUUID().toString(),null))
    }

    @Test
    internal fun `nao deve validar um UUID invalido`() {
        assertFalse(UUIDValidator().isValid("123231",null))
    }

    @Test
    internal fun `deve valida quando o valor for nulo`() {
        assertTrue(UUIDValidator().isValid(null,null))
    }
}