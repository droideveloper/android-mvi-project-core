package com.mvi.plugins

import com.android.build.api.variant.LibraryAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.Actions.with
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradleSubplugin

/**
 * Plugin that configures the UI module for the app.
 *
 * This plugin sets up Android and Compose-specific plugins by delegating to
 * [AndroidLibraryPlugin], applies the Compose compiler plugin, configures the
 * Android library DSL, and adds dependencies including core modules.
 */
class UiModulePlugin : Plugin<Project> {
    /**
     * Performs the plugin configuration within the target project scope.
     *
     * @param target The project to operate within
     */
    override fun apply(target: Project) {
        with(target) {
            /**
             * Applies the required Gradle plugins for Android library development.
             * - [AndroidLibraryPlugin]: Adds local Android plugin
             * - [ComposeCompilerGradleSubplugin]: Adds compose compiler plugin
             *
             * @param pluginManager The Gradle plugin manager
             */
            with(pluginManager) {
                apply(AndroidLibraryPlugin::class)
                apply(ComposeCompilerGradleSubplugin::class)
            }
            /**
             * Retrieves the LibraryExtension from the project extensions.
             * The LibraryExtension provides access to Android library-specific
             * configuration options.
             */
            val lib = extensions.getByType<LibraryAndroidComponentsExtension>()
            /**
             * Configures the Android library with common setup including:
             * - AndroidX libraries
             * - Material Components
             * - Lifecycle components
             * - Core Android dependencies
             *
             * @param target The project to configure
             * @param isCompose configure compose dependencies
             */
            lib.configureAndroidLibrary(target, true)

            /**
             * Configures the dependency management for the library.
             * Adds common dependencies required by all library modules.
             */
            dependencies {
                add("implementation", project(":core:mvi"))
                add("implementation", project(":core:injection:implementation"))

                add("testImplementation", kotlin("test"))
            }
        }
    }
}
