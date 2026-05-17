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

class DataModulePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            apply(JAVA_LIBRARY_PLUGIN_ID)
            apply(JVM_PLUGIN_ID)
            apply(KOTLINX_SERIALIZATION_PLUGIN_ID)

            with(pluginManager) {
                apply(KspGradleSubplugin::class)
            }

            val jvm = extensions.getByType<KotlinJvmExtension>()

            jvm.jvmToolchain {
                languageVersion.set(JavaLanguageVersion.of(11))
            }

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

internal val KOTLINX_SERIALIZATION_PLUGIN_ID = mapOf("plugin" to "org.jetbrains.kotlin.plugin.serialization")
