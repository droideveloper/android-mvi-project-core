plugins {
    alias(libs.plugins.mvi.library)
}

android {
    namespace = "com.mvi.core.datastore"
}

dependencies {
    api(projects.core.coroutines)
    api(projects.core.datastore.gateway)
    api(projects.core.environment.gateway)

    api(libs.kotlin.serialization.json)

    api(libs.datastore.android)
    api(libs.datastore.preferences.android)
}
