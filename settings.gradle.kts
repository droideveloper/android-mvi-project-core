rootProject.name = "PROJECT_NAME"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("plugins")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

include(":core:app")
include(":core:database:gateway")
include(":core:database:implementation")
include(":core:environment:gateway")
include(":core:environment:implementation")
include(":core:coroutines")
include(":core:injection:gateway")
include(":core:injection:implementation")
include(":core:location:gateway")
include(":core:location:implementation")
include(":core:kotlin")
include(":core:navigation:gateway")
include(":core:navigation:implementation")
include(":core:network")
include(":core:mvi")
include(":core:testing:gateway")
include(":core:testing:implementation")
include(":core:ui")
include(":core:repository")
include(":core:datastore:gateway")
include(":core:datastore:implementation")
