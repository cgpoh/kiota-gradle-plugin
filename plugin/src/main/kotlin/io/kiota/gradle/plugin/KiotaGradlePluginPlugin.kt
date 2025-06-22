package io.kiota.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.Plugin

class KiotaGradlePluginPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        // Register the extension
        val extension = project.extensions.create("kiota", KiotaExtension::class.java)
        extension.projectDir.set(project.layout.projectDirectory)

        // Register the task
        project.tasks.register("generateKiota", KiotaGenerateTask::class.java) { task ->
            task.group = "Kiota"
            task.description = "Generates code using Kiota"
            task.extension = extension
        }
    }
}
