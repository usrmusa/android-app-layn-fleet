package com.digilayn.laynfleet.core.auth

interface AuthService {
    fun currentUserId(): String?
    suspend fun signInWithEmail(email: String, password: String): Result<Unit>
    suspend fun signInWithGoogleIdToken(idToken: String): Result<Unit>
    fun signOut()
}

object SignedOutAuthService : AuthService {
    override fun currentUserId(): String? = null

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> =
        Result.failure(IllegalStateException("Firebase authentication is not configured."))

    override suspend fun signInWithGoogleIdToken(idToken: String): Result<Unit> =
        Result.failure(IllegalStateException("Firebase authentication is not configured."))

    override fun signOut() = Unit
}
