package io.kiota.gradle.plugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test

/**
 * A functional test for the 'io.kiota.kiota-gradle-plugin' plugin.
 */
class KiotaGradlePluginPluginFunctionalTest {

    @field:TempDir
    lateinit var projectDir: File

    private val buildFile by lazy { projectDir.resolve("build.gradle.kts") }
    private val settingsFile by lazy { projectDir.resolve("settings.gradle.kts") }

    @Test
    fun `can run generateKiota task`() {
        // Set up the test build
        settingsFile.writeText("""
            rootProject.name = "test-project"
        """.trimIndent())

        buildFile.writeText("""
            plugins {
                id("io.kiota.kiota-gradle-plugin")
            }

            kiota {
                file = file("openapi.yaml")
                targetDirectory = file("build/generated-sources/kiota")
                language = "Java"
                clientClass = "ApiClient"
                namespace = "com.apisdk"
            }
        """.trimIndent())

        // Create a dummy OpenAPI file
        val openApiFile = projectDir.resolve("openapi.yaml")
        openApiFile.writeText("""
            openapi: 3.0.0
            info:
              title: Test API
              version: 1.0.0
        """.trimIndent())

        // Run the build
        val runner = GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("generateKiota")
            .withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(result.output.contains("Kiota code generation completed"))
        assertTrue(projectDir.resolve("build/generated-sources/kiota").exists())
    }
}