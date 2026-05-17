plugins {
    alias(libs.plugins.mvi.common)
    alias(libs.plugins.ksp)
}

dependencies {
    api(projects.core.environment.gateway)
    api(projects.core.kotlin)

    api(libs.kotlin.coroutines.core)

    api(libs.reftorfit)
    api(libs.reftorfit.converter)

    api(libs.kotlin.serialization.core)
    api(libs.kotlin.serialization.json)

    api(libs.okhttp)
    api(libs.okhttp.logger)

    implementation(libs.dagger2)
    ksp(libs.dagger2.compiler)
}
