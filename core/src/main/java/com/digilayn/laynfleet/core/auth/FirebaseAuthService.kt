package com.digilayn.laynfleet.core.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class FirebaseAuthService(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
) : AuthService {
    override fun currentUserId(): String? = firebaseAuth.currentUser?.uid

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> =
        runCatching {
            firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
            Unit
        }.mapError()

    override suspend fun signInWithGoogleIdToken(idToken: String): Result<Unit> =
        runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            Unit
        }.mapError()

    override fun signOut() {
        firebaseAuth.signOut()
    }
}

private fun Result<Unit>.mapError(): Result<Unit> = fold(
    onSuccess = { Result.success(Unit) },
    onFailure = { Result.failure(AuthFailure(FirebaseAuthErrorMapper.fromThrowable(it))) },
)

class AuthFailure(val authError: AuthError) : RuntimeException(authError.message)
