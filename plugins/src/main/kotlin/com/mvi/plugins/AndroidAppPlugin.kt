package com.mvi.plugins

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.AppPlugin
import com.google.devtools.ksp.gradle.KspGradleSubplugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.Actions.with
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

/**
 * Gradle plugin for Android application modules with MVVM architecture.
 *
 * This plugin configures:
 * - Android Application plugin and Kotlin Android support
 * - Kotlin Symbol Processing (KSP) for code generation
 * - Compose Compiler for Jetpack Compose
 * - Core dependencies: Material Design, Dagger 2
 * - Project dependencies: :core:mvi, :core:injection modules
 *
 * @property target The Gradle project to configure
 */
class AndroidAppPlugin : Plugin<Project> {

    /**
     * Applies Android application plugins and configurations.
     *
     * Configures:
     * - Android Application plugin
     * - Kotlin Android support via KotlinAndroidPluginWrapper
     * - KSP for annotation processing
     * - Compose Compiler for Jetpack Compose
     * - Application-level Android configuration
     * - Android flavor configurations
     * - Core dependencies (Material, Dagger 2, project modules)
     *
     * @param target The project to apply Android app configuration to
     */
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(AppPlugin::class)
                apply(KotlinAndroidPluginWrapper::class)
                apply(KspGradleSubplugin::class)
                apply(ComposeCompilerGradleSubplugin::class)
            }

            /**
             * Retrieves the ApplicationExtension from the project extensions.
             * The ApplicationExtension provides access to Android application-specific
             * configuration options.
             */
            val app = extensions.getByType<ApplicationExtension>()
            /**
             * Configures the Android application with common setup including:
             * - AndroidX libraries
             * - Material Components
             * - Lifecycle components
             * - Core Android dependencies
             *
             * @param target The project to configure
             */
            app.configureAndroidApplication(target)
            /**
             * Configures build flavors for different build variants.
             * Sets up debug, release, and custom flavors as specified
             * in the project's flavor configuration.
             */
            app.configureFlavors()
            /**
             * Configures the dependency management for the application.
             * Adds common dependencies required by application module.
             */
            dependencies {
                add("implementation", material.toDependency())

                add("implementation", dagger2.toDependency())
                add("ksp", dagger2Compiler.toDependency())

                add("implementation", project(":core:mvi"))
                add("implementation", project(":core:injection:gateway"))
                add("implementation", project(":core:injection:implementation"))
            }
        }
    }
}
