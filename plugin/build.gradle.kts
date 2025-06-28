plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradle.plugin.publish)
}

repositories {
    mavenCentral()
    mavenLocal()
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("2.0.21")
        }

        // Create a new test suite
        val functionalTest by registering(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("2.0.21")

            dependencies {
                // functionalTest test suite depends on the production code in tests
                implementation(project())
            }

            targets {
                all {
                    // This test suite should run after the built-in test suite has run its tests
                    testTask.configure { shouldRunAfter(test) } 
                }
            }
        }
    }
}

// The project version used as the plugin version when publishing.
version = "0.0.1"
group = "io.github.cgpoh.kiota-gradle-plugin"

gradlePlugin {
    website.set("https://github.com/cgpoh/kiota-gradle-plugin")
    vcsUrl.set("https://github.com/cgpoh/kiota-gradle-plugin")
    plugins {
        create("kiotaGradlePlugin") {
            id = "io.github.cgpoh.kiota-gradle-plugin"
            implementationClass = "io.github.cgpoh.kiota.gradle.plugin.KiotaGradlePluginPlugin"
            displayName = "Kiota Gradle plugin"
            description = "Gradle plugin to generate code using Kiota"
            tags.set(listOf("kiota", "code generation", "openapi", "rest", "sdk", "client", "java"))
        }
    }
}

gradlePlugin.testSourceSets.add(sourceSets["functionalTest"])

tasks.named<Task>("check") {
    // Include functionalTest as part of the check lifecycle
    dependsOn(testing.suites.named("functionalTest"))
}
