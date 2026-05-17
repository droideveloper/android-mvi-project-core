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

class AndroidLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(LibraryPlugin::class)
                apply(KotlinAndroidPluginWrapper::class)
                apply(KspGradleSubplugin::class)
            }

            val lib = extensions.getByType<LibraryExtension>()
            lib.configureAndroidLibrary(target)
            lib.configureFlavors()

            dependencies {
                add("implementation", dagger2.toDependency())
                add("ksp", dagger2Compiler.toDependency())
            }
        }
    }
}
