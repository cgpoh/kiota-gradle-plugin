package io.kiota.gradle.plugin

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertNotNull
import kotlin.test.Test

/**
 * A unit test for the 'io.kiota.kiota-gradle-plugin' plugin.
 */
class KiotaGradlePluginPluginTest {
    @Test
    fun `plugin registers task`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("io.kiota.kiota-gradle-plugin")

        // Verify the result
        assertNotNull(project.tasks.findByName("generateKiota"))
    }
}