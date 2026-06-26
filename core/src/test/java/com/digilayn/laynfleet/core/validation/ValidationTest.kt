package com.digilayn.laynfleet.core.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidationTest {
    @Test fun returnsFirstFailingRuleMessage() {
        val rules = listOf(
            ValidationRules.required("Email is required."),
            ValidationRules.email("Enter a valid email address."),
        )

        assertEquals("Email is required.", validate("", rules))
    }

    @Test fun runsRulesInOrderAfterRequiredPasses() {
        val rules = listOf(
            ValidationRules.required("Email is required."),
            ValidationRules.email("Enter a valid email address."),
        )

        assertEquals("Enter a valid email address.", validate("lincoln", rules))
    }

    @Test fun acceptsValidEmail() {
        val rules = listOf(
            ValidationRules.required("Email is required."),
            ValidationRules.email("Enter a valid email address."),
        )

        assertNull(validate("lincoln@example.com", rules))
    }

    @Test fun supportsAppendingCustomRules() {
        val rules = listOf(
            ValidationRules.required("Password is required."),
            ValidationRules.minLength(8, "Password must be at least 8 characters."),
        )

        assertEquals("Password must be at least 8 characters.", validate("secret", rules))
    }
}
