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

    @Test fun fallsBackToGenericSignInMessage() {
        assertEquals(
            "We could not sign you in. Please try again.",
            FirebaseAuthErrorMapper.messageFor("SOMETHING_UNEXPECTED"),
        )
    }
}
