plugins {
    alias(libs.plugins.mvi.ui)
}

android {
    namespace = "com.mvi.core.location"
}

dependencies {
    api(projects.core.location.gateway)
    api(projects.core.environment.gateway)
    api(projects.core.ui)

    api(libs.kotlin.coroutines.core)

    api(libs.play.services.location)
    api(libs.play.services.coroutines)

    api(libs.androidx.activity.compose)
    implementation(libs.material3)

    ksp(libs.dagger2.compiler)
}
