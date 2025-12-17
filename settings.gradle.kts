// settings.gradle.kts (top-level)
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        // JitPack is not needed here unless you fetch plugins from JitPack.
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // Required for BlurView
    }
}

rootProject.name = "YourProjectName"
include(":app")
