plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

val mobileAppExportDirectory = providers.gradleProperty("laynFleetMobileAppDir")
    .orElse("/Users/lincoln.mgijima/Digilayn/Web/web-poortjie/laynfleet/operator/mobile-app")

tasks.register<Copy>("exportDevMobileApps") {
    group = "distribution"
    description = "Builds and exports LaynRiderTest.apk and LaynOperatorTest.apk."
    dependsOn(":laynrider:assembleDevDebug", ":laynoperator:assembleDevDebug")
    into(mobileAppExportDirectory)

    from("laynrider/build/outputs/apk/dev/debug/laynrider-dev-debug.apk") {
        rename("laynrider-dev-debug.apk", "LaynRiderTest.apk")
    }
    from("laynoperator/build/outputs/apk/dev/debug/laynoperator-dev-debug.apk") {
        rename("laynoperator-dev-debug.apk", "LaynOperatorTest.apk")
    }
}

tasks.register<Copy>("exportProdMobileApps") {
    group = "distribution"
    description = "Builds and exports LaynRider.apk and LaynOperator.apk."
    dependsOn(":laynrider:assembleProdRelease", ":laynoperator:assembleProdRelease")
    into(mobileAppExportDirectory)

    from("laynrider/build/outputs/apk/prod/release/laynrider-prod-release.apk") {
        rename("laynrider-prod-release.apk", "LaynRider.apk")
    }
    from("laynoperator/build/outputs/apk/prod/release/laynoperator-prod-release.apk") {
        rename("laynoperator-prod-release.apk", "LaynOperator.apk")
    }
}

tasks.register("exportMobileApps") {
    group = "distribution"
    description = "Builds and exports the dev/test and prod/release Layn Fleet APKs."
    dependsOn("exportDevMobileApps", "exportProdMobileApps")
}
