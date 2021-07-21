package br.com.zup.academy.pix

import java.util.*
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UUIDValidator::class])
annotation class UUIDValido(
    val message: String = "UUID invalido",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
)

@Singleton
class UUIDValidator: javax.validation.ConstraintValidator<UUIDValido, String> {

    override fun isValid(value: String?, context: javax.validation.ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }

        try {
            UUID.fromString(value)
            return true
        }catch (e: IllegalArgumentException){
            return false
        }
    }
}
