package com.mvi.plugins

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension

/**
 * Map of product flavor names to their application ID suffixes.
 *
 * @property name The flavor name (e.g., "mock", "staging", "prod")
 * @property suffix The application ID suffix to apply, or null for production build
 *
 * Flavors define different build configurations for testing environments:
 * - **mock**: Development environment with mock services
 * - **staging**: Pre-production environment for final testing
 * - **prod**: Production build with no suffix
 */
internal val flavors = mapOf(
    "mock" to ".mock",
    "staging" to ".staging",
    "prod" to null,
)

/**
 * Configures product flavors for different build environments.
 *
 * This function sets up product flavors for mock, staging, and production builds:
 * - Configures the "default" flavor dimension
 * - Creates product flavors based on the [flavors] map
 * - Applies application ID suffixes for non-production builds
 *
 * Product flavors enable different build configurations:
 * - **mock**: Development environment with mock services (e.g., `com.example.app.mock`)
 * - **staging**: Pre-production environment (e.g., `com.example.app.staging`)
 * - **prod**: Production build (e.g., `com.example.app`)
 *
 * @see flavors
 * @see applySuffixIfNeeded
 */
internal fun ApplicationAndroidComponentsExtension.configureFlavors() {
    finalizeDsl { extension ->
        with(extension) {
            flavorDimensions += "default"
            productFlavors {
                flavors.forEach { (name, suffix) ->
                    create(name) {
                        dimension = "default"
                        applySuffixIfNeeded(this, suffix)
                    }
                }
            }
        }
    }
}

/**
 * Configures product flavors for different build environments.
 *
 * This function sets up product flavors for mock, staging, and production builds:
 * - Configures the "default" flavor dimension
 * - Creates product flavors based on the [flavors] map
 * - Applies application ID suffixes for non-production builds
 *
 * Product flavors enable different build configurations:
 * - **mock**: Development environment with mock services (e.g., `com.example.app.mock`)
 * - **staging**: Pre-production environment (e.g., `com.example.app.staging`)
 * - **prod**: Production build (e.g., `com.example.app`)
 *
 * @see flavors
 */
internal fun LibraryAndroidComponentsExtension.configureFlavors() {
    finalizeDsl { extension ->
        with(extension) {
            flavorDimensions += "default"
            productFlavors {
                flavors.keys.forEach { name ->
                    create(name) {
                        dimension = "default"
                    }
                }
            }
        }
    }
}

/**
 * Applies an application ID suffix to a product flavor if a suffix is provided.
 *
 * This function conditionally applies an application ID suffix to product flavors
 * for non-production builds (mock, staging environments). For production builds,
 * no suffix is applied, keeping the standard application ID.
 *
 * Only applies the suffix when:
 * - The extension is an [ApplicationExtension]
 * - The [productFlavor] is an [ApplicationProductFlavor]
 * - A non-null suffix is provided
 *
 * @param productFlavor The product flavor to configure
 * @param suffix The application ID suffix to apply (e.g., ".mock", ".staging")
 *
 * @see flavors
 * @see configureFlavors
 */
internal fun ApplicationAndroidComponentsExtension.applySuffixIfNeeded(
    productFlavor: ProductFlavor,
    suffix: String? = null,
) {
    if (suffix != null) {
        if (productFlavor is ApplicationProductFlavor) {
            productFlavor.applicationIdSuffix = suffix
        }
    }
}
