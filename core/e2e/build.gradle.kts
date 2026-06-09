plugins {
    alias(libs.plugins.mvi.library)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.mvi.core.e2e"

    buildFeatures {
        compose = true
    }
}

dependencies {
    api(libs.kotlin.coroutines.test)
    api(libs.assertj)
    api(libs.mockk)
    api(libs.mockk.android)

    api(libs.junit)
    api(libs.androidx.test.junit)
    api(libs.androidx.test.manifest)
    api(libs.androidx.test.junit4)

    api(libs.bundles.espresso)
    api(libs.bundles.espresso.idling)

    api(libs.robolectric)
}
