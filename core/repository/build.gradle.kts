plugins {
    alias(libs.plugins.mvi.common)
}

dependencies {
    api(libs.kotlin.datetime)

    api(projects.core.coroutines)
}
