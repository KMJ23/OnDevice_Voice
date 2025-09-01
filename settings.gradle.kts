pluginManagement {
    includeBuild("build-logic")

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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven{ url = uri("https://jitpack.io") }
    }
}

gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:convention:testClasses"))

rootProject.name = "ondevice"
include(":app")
//include(":core:common")
//include(":core:data")
//include(":core:domain")
//include(":core:network")
//include(":core:model")
include(":core:database")
//include(":core:datastore")
include(":core:designsystem")
include(":core:rag")
include(":core:ui")
//include(":feature:chat")
//include(":feature:document")
//include(":lint")
include(":library:llama.cpp:llama")

