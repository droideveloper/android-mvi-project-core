package com.mvi.plugins

import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.google.devtools.ksp.gradle.KspGradleSubplugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.Actions.with
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

/**
 * Gradle plugin for Android library modules.
 *
 * Configures the following:
 * - Applies Android Library Plugin
 * - Applies Kotlin Android Plugin
 * - Applies KSP (Kotlin Symbol Processing)
 * - Configures Android library extensions
 * - Configures build flavors
 * - Adds Dagger2 dependency and KSP compiler
 *
 * @property target The Gradle project to configure
 */
class AndroidLibraryPlugin : Plugin<Project> {

    /**
     * Applies the plugin configuration to the target project.
     *
     * This function:
     * 1. Applies required Gradle plugins (Android, Kotlin Android, KSP)
     * 2. Configures the Android library extension
     * 3. Configures build flavors
     * 4. Adds Dagger2 dependency and its KSP compiler
     *
     * @param target The Gradle project to configure
     */
    override fun apply(target: Project) {
        /**
         * Performs the plugin configuration within the target project scope.
         *
         * @param target The project to operate within
         */
        with(target) {
            /**
             * Applies the required Gradle plugins for Android library development.
             * - [LibraryPlugin]: Adds Android library support
             * - [KotlinAndroidPluginWrapper]: Adds Kotlin with Android support
             * - [KspGradleSubplugin]: Enables Kotlin Symbol Processing
             *
             * @param pluginManager The Gradle plugin manager
             */
            with(pluginManager) {
                apply(LibraryPlugin::class)
                apply(KotlinAndroidPluginWrapper::class)
                apply(KspGradleSubplugin::class)
            }

            /**
             * Retrieves the LibraryExtension from the project extensions.
             * The LibraryExtension provides access to Android library-specific
             * configuration options.
             */
            val lib = extensions.getByType<LibraryExtension>()
            /**
             * Configures the Android library with common setup including:
             * - AndroidX libraries
             * - Material Components
             * - Lifecycle components
             * - Core Android dependencies
             *
             * @param target The project to configure
             */
            lib.configureAndroidLibrary(target)
            /**
             * Configures build flavors for different build variants.
             * Sets up debug, release, and custom flavors as specified
             * in the project's flavor configuration.
             */
            lib.configureFlavors()
            /**
             * Configures the dependency management for the library.
             * Adds common dependencies required by all library modules.
             */
            dependencies {
                add("implementation", dagger2.toDependency())
                add("ksp", dagger2Compiler.toDependency())
            }
        }
    }
}
