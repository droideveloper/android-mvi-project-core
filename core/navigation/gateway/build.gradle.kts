plugins {
    alias(libs.plugins.mvi.common)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.kotlin.serialization.core)
    implementation(libs.kotlin.serialization.json)
}
