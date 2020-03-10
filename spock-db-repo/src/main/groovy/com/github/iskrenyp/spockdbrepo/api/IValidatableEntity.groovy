package com.github.iskrenyp.spockdbrepo.api

import groovy.transform.TupleConstructor
import net.sf.oval.ConstraintViolation
import net.sf.oval.Validator
import net.sf.oval.configuration.Configurer
import net.sf.oval.exception.ValidationFailedException

@TupleConstructor
trait IValidatableEntity {

    Boolean validated(Collection<Configurer> configurers = null) {
        List<ConstraintViolation> violations = []
        try {
            violations = withValidator(configurers) { Validator validator -> validator.validate(this) }
        } catch ( IllegalArgumentException | ValidationFailedException e) {
            println("Validation for ${this.class.simpleName} failed due to: $e")
            return false
        }
        if (!violations.empty) {
            violations.each { println("Validation for ${this.class.simpleName} failed due to: $it.message, $it.invalidValue") }
            return false
        } else return true
    }

    def <T> T withValidator(Collection<Configurer> configurers = null, Closure < T > consumeValidator) {
        configurers ? new Validator(configurers).with(consumeValidator) : new Validator().with(consumeValidator)
    }

    List<ConstraintViolation> constraintViolations() {
        try {
            new Validator().validate(this)
        } catch ( IllegalArgumentException | ValidationFailedException e) {
            println("Validation for ${this.class.simpleName} failed due to: $e")
            null
        }
    }

}