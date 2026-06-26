package com.digilayn.laynfleet.core.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleCredentialSignIn(
    private val serverClientId: String,
) {
    suspend fun requestIdToken(context: Context): Result<String> = runCatching {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val response = CredentialManager.create(context).getCredential(
            context = context,
            request = request,
        )

        GoogleIdTokenCredential.createFrom(response.credential.data).idToken
    }.recoverCatching { throwable ->
        if (throwable is GetCredentialCancellationException) {
            throw AuthFailure(AuthError("GOOGLE_SIGN_IN_CANCELLED", FirebaseAuthErrorMapper.messageFor("GOOGLE_SIGN_IN_CANCELLED")))
        }
        throw AuthFailure(AuthError("GOOGLE_SIGN_IN_FAILED", FirebaseAuthErrorMapper.messageFor("GOOGLE_SIGN_IN_FAILED")))
    }
}
