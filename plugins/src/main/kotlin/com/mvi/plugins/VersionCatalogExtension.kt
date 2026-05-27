package com.mvi.plugins

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType
import java.util.Optional

/**
 * Extension property to access the named "libs" version catalog from the project extensions.
 *
 * This provides a convenient way to access the version catalog defined in libs.toml.
 *
 * @return The [VersionCatalog] instance from the project's extensions.
 */
internal val Project.libs
    get(): VersionCatalog =
        extensions
            .getByType<VersionCatalogsExtension>()
            .named("libs")

// app version sets from libs.toml
/**
 * Extension property to get the compile SDK version from the version catalog.
 *
 * @return The compile SDK version string from libs.toml.
 */
internal val Project.compileSdkVersion
    get() = libs.findVersion("android.compileSdk")

/**
 * Extension property to get the target SDK version from the version catalog.
 *
 * @return The target SDK version string from libs.toml.
 */
internal val Project.targetSdkVersion
    get() = libs.findVersion("android.targetSdk")

/**
 * Extension property to get the minimum SDK version from the version catalog.
 *
 * @return The minimum SDK version string from libs.toml.
 */
internal val Project.minSdkVersion
    get() = libs.findVersion("android.minSdk")


/**
 * Converts an optional [Provider] of module dependency to a [MinimalExternalModuleDependency].
 *
 * @return The [MinimalExternalModuleDependency] or throws exception if not present.
 */
internal fun Optional<Provider<MinimalExternalModuleDependency>>.toDependency() =
    this.get()

/**
 * Converts an optional [VersionConstraint] to an integer version.
 *
 * @return The integer version from the constraint's display name.
 */
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
