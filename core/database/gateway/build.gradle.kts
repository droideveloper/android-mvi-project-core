plugins {
    alias(libs.plugins.mvi.common)
}

dependencies {
    api(projects.core.kotlin)

    api(libs.room.runtime)
}
