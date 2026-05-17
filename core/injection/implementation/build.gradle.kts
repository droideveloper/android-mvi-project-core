plugins {
    alias(libs.plugins.mvi.library)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.mvi.core.injection"
    buildFeatures {
        compose = true
    }
}

dependencies {
    api(projects.core.injection.gateway)

    api(libs.compose.runtime)

    api(libs.androidx.lifecycle.runtime.compose)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.lifecycle.viewmodel.compose)
    api(libs.androidx.lifecycle.viewmodel.savedstate)

    implementation(libs.dagger2)
}
