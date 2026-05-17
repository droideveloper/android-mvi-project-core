plugins {
    alias(libs.plugins.mvi.library)
}

android {
    namespace = "com.mvi.core.mvi"
}

dependencies {
    api(projects.core.coroutines)

    api(libs.androidx.lifecycle.runtime.compose)
    api(libs.androidx.lifecycle.viewmodel)

    api(libs.kotlin.datetime)
}
