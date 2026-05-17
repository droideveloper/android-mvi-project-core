package com.mvi.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension

class CommonModulePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            apply(JAVA_LIBRARY_PLUGIN_ID)
            apply(JVM_PLUGIN_ID)

            val jvm = extensions.getByType<KotlinJvmExtension>()
            jvm.jvmToolchain {
                languageVersion.set(JavaLanguageVersion.of(11))
            }

            dependencies {
                add("implementation", kotlinStdlib.toDependency())
                add("implementation", kotlinCoroutinesCore.toDependency())

                add("testImplementation", kotlin("test"))
            }
        }
    }
}

internal val JVM_PLUGIN_ID = mapOf("plugin" to "org.jetbrains.kotlin.jvm")
internal val JAVA_LIBRARY_PLUGIN_ID = mapOf("plugin" to "java-library")
