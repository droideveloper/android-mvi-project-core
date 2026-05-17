plugins {
    alias(libs.plugins.mvi.library)
}

android {
    namespace = "com.mvi.core.environment"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(projects.core.environment.gateway)

    api(projects.core.injection.gateway)
}
