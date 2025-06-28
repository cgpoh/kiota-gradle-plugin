package io.github.cgpoh.kiota.gradle.plugin

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions
import kotlin.test.Test

/**
 * A unit test for the 'io.kiota.kiota-gradle-plugin' plugin.
 */
class KiotaGradlePluginPluginTest {
    @Test
    fun `plugin registers task`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("io.github.cgpoh.kiota-gradle-plugin")

        // Verify the result
        Assertions.assertNotNull(project.tasks.findByName("generateKiota"))
    }
}