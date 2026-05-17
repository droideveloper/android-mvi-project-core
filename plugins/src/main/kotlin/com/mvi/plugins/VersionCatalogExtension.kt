package com.mvi.plugins

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType
import java.util.Optional

internal val Project.libs
    get(): VersionCatalog =
        extensions
            .getByType<VersionCatalogsExtension>()
            .named("libs")

// app version sets from libs.toml
internal val Project.compileSdkVersion
    get() = libs.findVersion("android.compileSdk")

internal val Project.targetSdkVersion
    get() = libs.findVersion("android.targetSdk")

internal val Project.minSdkVersion
    get() = libs.findVersion("android.minSdk")


internal fun Optional<Provider<MinimalExternalModuleDependency>>.toDependency() =
    this.get()

internal fun Optional<VersionConstraint>.toInt() =
    this.get()
        .displayName
        .toInt()

internal val Project.coreDesugarLibrary
    get() = libs.findLibrary("android.desugar.jdk.libs")

internal val Project.composeUi
    get() = libs.findLibrary("compose.ui")

internal val Project.composeRuntime
    get() = libs.findLibrary("compose.runtime")

internal val Project.composeFoundation
    get() = libs.findLibrary("compose.foundation")

internal val Project.composeTooling
    get() = libs.findLibrary("compose.tooling")

internal val Project.composeToolingPreview
    get() = libs.findLibrary("compose.tooling.preview")

internal val Project.kotlinSerializationCore
    get() = libs.findLibrary("kotlin.serialization.core")
internal val Project.kotlinSerializationJson
    get() = libs.findLibrary("kotlin.serialization.json")
internal val Project.kotlinCoroutinesCore
    get() = libs.findLibrary("kotlin.coroutines.core")
internal val Project.kotlinDatetime
    get() = libs.findLibrary("kotlin.datetime")

internal val Project.kotlinStdlib
    get() = libs.findLibrary("kotlin.stdlib")

internal val Project.dagger2
    get() = libs.findLibrary("dagger2")

internal val Project.dagger2Compiler
    get() = libs.findLibrary("dagger2.compiler")


internal val Project.material
    get() = libs.findLibrary("material")
