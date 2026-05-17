plugins {
    alias(libs.plugins.mvi.common)
}

dependencies {
    api(libs.kotlin.coroutines.core)
    api(libs.kotlin.datetime)

    implementation(libs.dagger2)
}
