package com.digilayn.laynfleet.core.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.digilayn.laynfleet.core.util.FlowLogger
import kotlinx.coroutines.tasks.await

class FirebaseAuthService(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
) : AuthService {
    override fun currentUserId(): String? = firebaseAuth.currentUser?.uid

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> =
        runCatching {
            FlowLogger.d("FirebaseAuthService", "Attempting email sign-in for $email")
            firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
            FlowLogger.i("FirebaseAuthService", "Email sign-in successful for $email")
            Unit
        }.onFailure {
            FlowLogger.e("FirebaseAuthService", "Email sign-in failed for $email", it)
        }.mapError()

    override suspend fun createAccountWithEmail(
        email: String,
        password: String,
    ): Result<AuthenticatedUser> =
        runCatching {
            FlowLogger.d("FirebaseAuthService", "Attempting account creation for $email")
            val result = firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = requireNotNull(result.user) {
                "Firebase did not return a user after account creation."
            }
            FlowLogger.i("FirebaseAuthService", "Account creation successful for $email, UID: ${user.uid}")
            AuthenticatedUser(
                userId = user.uid,
                email = user.email ?: email.trim(),
            )
        }.onFailure {
            FlowLogger.e("FirebaseAuthService", "Account creation failed for $email", it)
        }.mapAccountError()

    override suspend fun signInWithGoogleIdToken(idToken: String): Result<Unit> =
        runCatching {
            FlowLogger.d("FirebaseAuthService", "Attempting Google sign-in")
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            FlowLogger.i("FirebaseAuthService", "Google sign-in successful")
            Unit
        }.onFailure {
            FlowLogger.e("FirebaseAuthService", "Google sign-in failed", it)
        }.mapError()

    override fun signOut() {
        FlowLogger.i("FirebaseAuthService", "Signing out")
        firebaseAuth.signOut()
    }
}

private fun Result<Unit>.mapError(): Result<Unit> = fold(
    onSuccess = { Result.success(Unit) },
    onFailure = { Result.failure(AuthFailure(FirebaseAuthErrorMapper.fromThrowable(it))) },
)

private fun Result<AuthenticatedUser>.mapAccountError(): Result<AuthenticatedUser> = fold(
    onSuccess = { Result.success(it) },
    onFailure = { Result.failure(AuthFailure(FirebaseAuthErrorMapper.fromThrowable(it))) },
)

class AuthFailure(val authError: AuthError) : RuntimeException(authError.message)
