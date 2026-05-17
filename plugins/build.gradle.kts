plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.android.tools.common.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.compose.compiler.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("mvi-common") {
            id = "com.mvi.common"
            implementationClass = "com.mvi.plugins.CommonModulePlugin"
        }
        register("mvi-data") {
            id = "com.mvi.data"
            implementationClass = "com.mvi.plugins.DataModulePlugin"
        }
        register("mvi-domain") {
            id =  "com.mvi.domain"
            implementationClass = "com.mvi.plugins.DomainModulePlugin"
        }
        register("mvi-ui") {
            id =  "com.mvi.ui"
            implementationClass = "com.mvi.plugins.UiModulePlugin"
        }
        register("mvi-library") {
            id =  "com.mvi.library"
            implementationClass = "com.mvi.plugins.AndroidLibraryPlugin"
        }
        register("mvi-app") {
            id =  "com.mvi.app"
            implementationClass = "com.mvi.plugins.AndroidAppPlugin"
        }
    }
}
