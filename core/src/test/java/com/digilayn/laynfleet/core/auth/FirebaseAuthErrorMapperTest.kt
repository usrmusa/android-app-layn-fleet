package com.digilayn.laynfleet.core.auth

import org.junit.Assert.assertEquals
import org.junit.Test

class FirebaseAuthErrorMapperTest {
    @Test fun mapsInvalidCredentialToUserSafeMessage() {
        assertEquals(
            "The email or password is incorrect.",
            FirebaseAuthErrorMapper.messageFor("ERROR_INVALID_CREDENTIAL"),
        )
    }

    @Test fun mapsNetworkFailureToRetryableMessage() {
        assertEquals(
            "Check your connection and try again.",
            FirebaseAuthErrorMapper.messageFor("ERROR_NETWORK_REQUEST_FAILED"),
        )
    }

    @Test fun mapsEmailAlreadyInUseToLoginHint() {
        assertEquals(
            "This email is already registered. Log in instead.",
            FirebaseAuthErrorMapper.messageFor("ERROR_EMAIL_ALREADY_IN_USE"),
        )
    }

    @Test fun mapsWeakPasswordToMinimumLengthMessage() {
        assertEquals(
            "Password must be at least 6 characters.",
            FirebaseAuthErrorMapper.messageFor("ERROR_WEAK_PASSWORD"),
        )
    }

    @Test fun mapsDisabledEmailRegistrationToSupportMessage() {
        assertEquals(
            "Email and password registration is not enabled. Contact support.",
            FirebaseAuthErrorMapper.messageFor("ERROR_OPERATION_NOT_ALLOWED"),
        )
    }

    @Test fun fallsBackToGenericSignInMessage() {
        assertEquals(
            "We could not sign you in. Please try again.",
            FirebaseAuthErrorMapper.messageFor("SOMETHING_UNEXPECTED"),
        )
    }
}
