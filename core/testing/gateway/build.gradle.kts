plugins {
    alias(libs.plugins.mvi.common)
}

dependencies {
    api(libs.junit)
    api(libs.kotlin.coroutines.test)
    api(libs.assertj)
    api(libs.mockk)
    api(libs.mockk.jvm)

    api(kotlin("test"))
    api(kotlin("test-junit"))
}
