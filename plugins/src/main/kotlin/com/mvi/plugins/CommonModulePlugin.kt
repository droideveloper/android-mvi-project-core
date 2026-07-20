package com.mvi.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension

/**
 * Plugin that configures the common module for the app.
 *
 * This plugin sets up the JVM toolchain, applies the Java library and Kotlin JVM plugins,
 * and adds Kotlin standard library, coroutines, and testing dependencies to the project.
 */
class CommonModulePlugin : Plugin<Project> {

    /**
     * Configures the common module for the app by setting up toolchains, plugins, and dependencies.
     *
     * @param target The project to configure
     */
    override fun apply(target: Project) {
        with(target) {
            apply(JAVA_LIBRARY_PLUGIN_ID)
            apply(JVM_PLUGIN_ID)
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
             * Configures the dependency management for the jvm-library.
             * Adds common dependencies required by all jvm-library modules.
             */
            dependencies {
                add("implementation", kotlinStdlib.toDependency())
                add("implementation", kotlinCoroutinesCore.toDependency())

                add("testImplementation", kotlin("test"))
            }
        }
    }
}

/**
 * The plugin ID map for the Kotlin JVM plugin.
 */
internal val JVM_PLUGIN_ID = mapOf("plugin" to "org.jetbrains.kotlin.jvm")

/**
 * The plugin ID map for the Java library plugin.
 */
internal val JAVA_LIBRARY_PLUGIN_ID = mapOf("plugin" to "java-library")
