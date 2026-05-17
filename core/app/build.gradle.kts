plugins {
    alias(libs.plugins.mvi.library)
}

android {
    namespace = "com.mvi.core.app"
}

dependencies {
    api(projects.core.coroutines)

    api(projects.core.environment.gateway)
    api(projects.core.environment.implementation)

    implementation(libs.dagger2)
    ksp(libs.dagger2.compiler)
}
