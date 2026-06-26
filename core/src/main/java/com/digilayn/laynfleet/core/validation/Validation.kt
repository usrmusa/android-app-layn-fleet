package com.digilayn.laynfleet.core.validation

data class ValidationRule(
    val message: String,
    val isValid: (String) -> Boolean,
)

object ValidationRules {
    private val emailPattern = Regex("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", RegexOption.IGNORE_CASE)

    fun required(message: String): ValidationRule = ValidationRule(message) {
        it.isNotBlank()
    }

    fun email(message: String): ValidationRule = ValidationRule(message) {
        it.isBlank() || emailPattern.matches(it.trim())
    }

    fun minLength(length: Int, message: String): ValidationRule = ValidationRule(message) {
        it.length >= length
    }

    fun matches(expected: () -> String, message: String): ValidationRule = ValidationRule(message) {
        it == expected()
    }
}

fun validate(value: String, rules: List<ValidationRule>): String? =
    rules.firstOrNull { !it.isValid(value) }?.message
