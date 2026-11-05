plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

// So that `./gradlew testDebugUnitTest` at the root runs these tests too.
tasks.register("testDebugUnitTest") {
    dependsOn(tasks.test)
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
}
