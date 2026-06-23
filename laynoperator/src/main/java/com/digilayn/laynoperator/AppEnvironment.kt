package com.digilayn.laynoperator

data class AppEnvironment(
    val name: String,
    val firestoreNamespace: String,
    val isProduction: Boolean,
) {
    companion object {
        val current = AppEnvironment(
            name = BuildConfig.ENVIRONMENT,
            firestoreNamespace = BuildConfig.FIRESTORE_NAMESPACE,
            isProduction = BuildConfig.IS_PRODUCTION,
        )
    }
}
