package com.mvi.plugins

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension

/**
 * Configures the application-level Android build.
 *
 * Sets up application-specific configurations including namespace, default config,
 * build types (debug/release), packaging options, test options, and delegates to
 * [configureAndroidLibrary] for library configuration.
 *
 * @param target The project to configure as an Android application
 */
internal fun ApplicationExtension.configureAndroidApplication(
    target: Project,
) {
    defaultConfig {
        targetSdk = target.targetSdkVersion.toInt()
        versionCode = 1
        versionName = "1.0.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        animationsDisabled = true
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    configureAndroidLibrary(target, true)
}

/**
 * Configures common Android build settings shared by applications and libraries.
 *
 * Sets up compile SDK, Compose features (if enabled), default config with min SDK,
 * compile options with Java 11, test options, Kotlin JVM toolchain with Java 11,
 * and relevant dependencies (core library desugaring, Compose runtime, testing).
 *
 * @param target The project to configure
 * @param isCompose Whether to enable Compose features and add Compose dependencies
 */
internal fun CommonExtension<*,*,*,*,*,*>.configureAndroidLibrary(
    target: Project,
    isCompose: Boolean = false,
) {
    val isApplication = this is ApplicationExtension

    compileSdk = target.compileSdkVersion.toInt()

    buildFeatures {
        compose = isCompose
    }

    defaultConfig {
        minSdk = target.minSdkVersion.toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }

    testOptions {
        if (isApplication.not()) {
            targetSdk = target.targetSdkVersion.toInt()
        }
        animationsDisabled = true
        unitTests {
            isIncludeAndroidResources = target.isAndroidResourcesShouldIncluded()
            isReturnDefaultValues = true
        }
    }

    with(target) {
        extensions.getByType<KotlinBaseExtension>().apply {
            jvmToolchain {
                languageVersion.set(JavaLanguageVersion.of(11))
            }
        }

        dependencies {
            add("coreLibraryDesugaring", coreDesugarLibrary.toDependency())

            if (isCompose) {
                add("implementation", composeRuntime.toDependency())
                add("implementation", composeFoundation.toDependency())
                add("implementation", composeUi.toDependency())
                add("implementation", composeTooling.toDependency())
                add("debugImplementation", composeToolingPreview.toDependency())
            }

            add("testImplementation", kotlin("test"))
        }
    }
}

/**
 * Checks whether Android resources should be included for unit tests.
 *
 * Returns true if either the `src/androidUnitTest` or `src/test` directories exist,
 * indicating that this is a test target that should include Android resources.
 *
 * @return true if Android resources should be included, false otherwise
 */
internal fun Project.isAndroidResourcesShouldIncluded(): Boolean =
    layout.projectDirectory.dir("src/androidUnitTest").asFile.exists() ||
        layout.projectDirectory.dir("src/test").asFile.exists()
