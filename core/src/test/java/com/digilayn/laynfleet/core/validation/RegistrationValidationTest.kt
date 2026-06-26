package com.digilayn.laynfleet.core.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RegistrationValidationTest {
    @Test fun rejectsPasswordShorterThanSixCharacters() {
        val rules = listOf(
            ValidationRules.required("Password is required."),
            ValidationRules.minLength(6, "Password must be at least 6 characters."),
        )

        assertEquals("Password must be at least 6 characters.", validate("12345", rules))
    }

    @Test fun acceptsSixCharacterPassword() {
        val rules = listOf(
            ValidationRules.required("Password is required."),
            ValidationRules.minLength(6, "Password must be at least 6 characters."),
        )

        assertNull(validate("123456", rules))
    }

    @Test fun rejectsMismatchedConfirmPassword() {
        val password = "123456"
        val rules = listOf(
            ValidationRules.required("Confirm password is required."),
            ValidationRules.matches({ password }, "Passwords must match."),
        )

        assertEquals("Passwords must match.", validate("123457", rules))
    }

    @Test fun acceptsMatchingConfirmPassword() {
        val password = "123456"
        val rules = listOf(
            ValidationRules.required("Confirm password is required."),
            ValidationRules.matches({ password }, "Passwords must match."),
        )

        assertNull(validate("123456", rules))
    }
}
