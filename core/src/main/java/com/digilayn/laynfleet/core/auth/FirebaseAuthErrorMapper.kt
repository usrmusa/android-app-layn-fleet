package com.digilayn.laynfleet.core.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException

object FirebaseAuthErrorMapper {
    fun fromThrowable(throwable: Throwable): AuthError {
        val code = when (throwable) {
            is FirebaseAuthException -> throwable.errorCode
            is FirebaseNetworkException -> "ERROR_NETWORK_REQUEST_FAILED"
            else -> throwable.message.orEmpty()
        }
        return AuthError(code = code, message = messageFor(code))
    }

    fun messageFor(code: String): String = when (code) {
        "ERROR_INVALID_EMAIL" -> "Enter a valid email address."
        "ERROR_USER_NOT_FOUND" -> "No account exists with this email."
        "ERROR_WRONG_PASSWORD" -> "The password is incorrect."
        "ERROR_INVALID_CREDENTIAL" -> "The email or password is incorrect."
        "ERROR_USER_DISABLED" -> "This account has been disabled. Contact support."
        "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Try again later."
        "ERROR_NETWORK_REQUEST_FAILED" -> "Check your connection and try again."
        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" ->
            "This email is already linked to another sign-in method."
        "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already registered. Log in instead."
        "ERROR_WEAK_PASSWORD" -> "Password must be at least 6 characters."
        "ERROR_OPERATION_NOT_ALLOWED" ->
            "Email and password registration is not enabled. Contact support."
        "GOOGLE_SIGN_IN_CANCELLED" -> "Google sign-in was cancelled."
        "GOOGLE_SIGN_IN_FAILED" -> "Google sign-in could not be completed. Try again."
        else -> "We could not sign you in. Please try again."
    }
}
