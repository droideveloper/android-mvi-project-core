plugins {
    alias(libs.plugins.mvi.common)
}

dependencies {
    implementation(projects.core.kotlin)

    api(libs.room.runtime)
}
