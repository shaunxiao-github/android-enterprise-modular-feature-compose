pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "EnterpriseAppCompose"

include(":app")
include(":features:bundle")

include(":core:common")
include(":core:network")
include(":core:config")
include(":core:navigation")
include(":core:ui")

include(":feature:payments:api")
include(":feature:payments:impl")
include(":feature:profile:api")
include(":feature:profile:impl")
