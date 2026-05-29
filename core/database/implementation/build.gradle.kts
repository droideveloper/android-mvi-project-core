plugins {
    alias(libs.plugins.mvi.library)
}

android {
    namespace = "com.mvi.core.database"
}

dependencies {
    api(projects.core.database.gateway)
    api(projects.core.environment.gateway)

    implementation(libs.room.coroutines)
}
