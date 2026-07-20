package com.mvi.plugins

import com.google.devtools.ksp.gradle.KspGradleSubplugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension

/**
 * Plugin that configures the data module for the app.
 *
 * This plugin sets up the JVM toolchain, applies the Java library, Kotlin JVM,
 * and KotlinX serialization plugins, and configures KSP (Kotlin Symbol Processing).
 * It adds dependencies including Kotlin standard library, coroutines, serialization,
 * date-time, Dagger 2 for dependency injection, and testing libraries.
 */
class DataModulePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        /**
         * Configures the data module for the app by setting up plugins, toolchains, and dependencies.
         * Applies Java library, Kotlin JVM, and KotlinX serialization plugins, configures KSP,
         * sets up the JVM toolchain to Java 11, and adds Kotlin, coroutines, serialization,
         * date-time, and Dagger dependencies.
         *
         * @param target The project to configure
         */
        with(target) {
            apply(JAVA_LIBRARY_PLUGIN_ID)
            apply(JVM_PLUGIN_ID)
            apply(KOTLINX_SERIALIZATION_PLUGIN_ID)

            /**
             * Applies the required Gradle plugins for data development.
             * - [KspGradleSubplugin]: Enables Kotlin Symbol Processing
             *
             * @param pluginManager The Gradle plugin manager
             */
            with(pluginManager) {
                apply(KspGradleSubplugin::class)
            }
            /**
             * Retrieves the KotlinJvmExtension from the project extensions.
             * The KotlinJvmExtension provides access to jvm library-specific
             * configuration options.
             */
            val jvm = extensions.getByType<KotlinJvmExtension>()

            jvm.jvmToolchain {
                languageVersion.set(JavaLanguageVersion.of(javaVersion))
            }
            /**
             * Configures the dependency management for the data-jvm-library.
             * Adds common dependencies required by all data-jvm-library modules.
             */
            dependencies {
                add("implementation", kotlinStdlib.toDependency())

                add("implementation", kotlinCoroutinesCore.toDependency())
                add("implementation", kotlinSerializationCore.toDependency())
                add("implementation", kotlinSerializationJson.toDependency())
                add("implementation", kotlinDatetime.toDependency())

                add("implementation", dagger2.toDependency())
                add("ksp", dagger2Compiler.toDependency())

                add("testImplementation", kotlin("test"))
            }
        }
    }
}

/**
 * The plugin ID map for the Kotlin Serialization plugin.
 */
internal val KOTLINX_SERIALIZATION_PLUGIN_ID = mapOf("plugin" to "org.jetbrains.kotlin.plugin.serialization")
