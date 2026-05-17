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

class AndroidAppPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(AppPlugin::class)
                apply(KotlinAndroidPluginWrapper::class)
                apply(KspGradleSubplugin::class)
                apply(ComposeCompilerGradleSubplugin::class)
            }

            val app = extensions.getByType<ApplicationExtension>()
            app.configureAndroidApplication(target)
            app.configureFlavors()

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
