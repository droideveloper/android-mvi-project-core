plugins {
    alias(libs.plugins.mvi.ui)
}

android {
    namespace = "com.mvi.core.navigation"
}

dependencies {
    api(projects.core.navigation.gateway)

    api(libs.androidx.navigation.compose)
}
