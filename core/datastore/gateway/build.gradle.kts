plugins {
    alias(libs.plugins.mvi.common)
}

dependencies {
    api(projects.core.kotlin)

    api(libs.datastore.core)
    api(libs.datastore.preferences.core)
    api(libs.kotlin.serialization.core)
}
