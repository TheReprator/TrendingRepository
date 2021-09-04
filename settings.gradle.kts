dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

include(
    ":app",
    ":base",
    ":base-android",
    ":navigation",
    ":appModules:trendingList"
)

rootProject.buildFileName = "build.gradle.kts"
rootProject.name = "TrendingRepository"
