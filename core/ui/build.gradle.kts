plugins {
    alias(libs.plugins.mvi.ui)
}

android {
    namespace = "com.mvi.core.ui"
}

dependencies {
    api(projects.core.environment.implementation)

    api(libs.material3)
    api(libs.material.icons.core)

    implementation(libs.compose.tooling)
    debugImplementation(libs.compose.tooling.preview)
}
