package com.mvi.plugins

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.Actions.with
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradleSubplugin

class UiModulePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(AndroidLibraryPlugin::class)
                apply(ComposeCompilerGradleSubplugin::class)
            }

            val lib = extensions.getByType<LibraryExtension>()
            lib.configureAndroidLibrary(target, true)

            dependencies {
                add("implementation", project(":core:mvi"))
                add("implementation", project(":core:injection:implementation"))

                add("testImplementation", kotlin("test"))
            }
        }
    }
}
