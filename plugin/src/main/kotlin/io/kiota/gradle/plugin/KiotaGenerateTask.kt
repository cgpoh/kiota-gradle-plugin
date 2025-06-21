package io.kiota.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URL
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

abstract class KiotaGenerateTask : DefaultTask() {
    @Input
    lateinit var extension: KiotaExtension

    @TaskAction
    fun generate() {
        if (extension.skip) {
            logger.lifecycle("Skipping Kiota code generation")
            return
        }

        val executable = if (extension.useSystemKiota) {
            "kiota"
        } else {
            val executablePath = Paths.get(
                extension.targetBinaryFolder.absolutePath,
                extension.kiotaVersion
            ).toFile()
            downloadAndExtract(
                "${extension.baseURL}/v${extension.kiotaVersion}/${resolveArtifact()}.zip",
                executablePath
            )
            Paths.get(executablePath.absolutePath, resolveBinary()).toFile().absolutePath
        }

        val openApiSpec = resolveOpenApiSpec()
        executeKiota(executable, openApiSpec)
    }

    private fun resolveArtifact(): String {
        val os = when {
            extension.osName.startsWith("Linux", true) -> "linux"
            extension.osName.startsWith("Mac", true) -> "osx"
            extension.osName.startsWith("Win", true) -> "win"
            else -> throw IllegalArgumentException("Unsupported OS: ${extension.osName}")
        }
        val arch = when {
            extension.osArch.contains("64") -> "x64"
            extension.osArch.contains("86") -> "x86"
            else -> throw IllegalArgumentException("Unsupported architecture: ${extension.osArch}")
        }
        return "$os-$arch"
    }

    private fun resolveBinary(): String {
        return if (extension.osName.startsWith("Win", true)) "kiota.exe" else "kiota"
    }

    private fun resolveOpenApiSpec(): File {
        return when {
            extension.file != null && extension.url != null -> throw IllegalArgumentException("Provide only one of 'file' or 'url'")
            extension.file != null -> extension.file!!
            extension.url != null -> downloadSpec(extension.url!!)
            else -> throw IllegalArgumentException("Provide one of 'file' or 'url'")
        }
    }

    private fun downloadSpec(url: URL): File {
        extension.downloadTarget.mkdirs()
        val destination = File(extension.downloadTarget, File(url.file).name)
        if (destination.exists()) {
            logger.warn("Skipping download of $url as it already exists at $destination")
            return destination
        }
        url.openStream().use { input ->
            destination.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return destination
    }

    private fun downloadAndExtract(url: String, dest: File) {
        val zipFile = File(dest, "kiota.zip")
        val finalDestination = File(dest, resolveBinary())
        if (!finalDestination.exists()) {
            dest.mkdirs()
            URL(url).openStream().use { input ->
                zipFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            FileSystems.newFileSystem(zipFile.toPath(), emptyMap<String, Any?>()).use { fs ->
                Files.copy(fs.getPath("/${resolveBinary()}"), finalDestination.toPath())
            }
            finalDestination.setExecutable(true, false)
            zipFile.delete()
        }
    }

    private fun executeKiota(binary: String, openApiSpec: File) {
        if (!openApiSpec.exists()) {
            throw IllegalArgumentException("Spec file not found: ${openApiSpec.absolutePath}")
        }
        val cmd = mutableListOf(
            binary, "generate",
            "--openapi", openApiSpec.absolutePath,
            "--output", extension.targetDirectory.absolutePath,
            "--language", extension.language,
            "--class-name", extension.clientClass,
            "--namespace-name", extension.namespace
        )
        extension.serializers.forEach { cmd.addAll(listOf("--serializer", it)) }
        extension.deserializers.forEach { cmd.addAll(listOf("--deserializer", it)) }
        extension.includePath?.forEach { cmd.addAll(listOf("--include-path", it)) }
        extension.excludePath?.forEach { cmd.addAll(listOf("--exclude-path", it)) }
        if (extension.cleanOutput) cmd.add("--clean-output")
        if (extension.clearCache) cmd.add("--clear-cache")
        cmd.addAll(listOf("--log-level", extension.kiotaLogLevel))

        val process = ProcessBuilder(cmd).apply {
            directory(project.projectDir)
            inheritIO()
        }.start()
        if (!process.waitFor(extension.kiotaTimeout.toLong(), TimeUnit.SECONDS)) {
            throw RuntimeException("Kiota process timed out")
        }
        if (process.exitValue() != 0) {
            throw RuntimeException("Kiota process failed with exit code ${process.exitValue()}")
        }
        project.logger.lifecycle("Kiota code generation completed")
    }
}