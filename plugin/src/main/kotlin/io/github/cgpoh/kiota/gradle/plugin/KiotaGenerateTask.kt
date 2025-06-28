package io.github.cgpoh.kiota.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URL
import java.nio.file.FileSystems
import java.nio.file.Files
import java.util.concurrent.TimeUnit

abstract class KiotaGenerateTask : DefaultTask() {

    @Internal
    lateinit var extension: KiotaExtension

    @TaskAction
    fun generate() {
        if (extension.skip.get()) {
            logger.lifecycle("Skipping Kiota code generation")
            return
        }

        val executablePath = if (extension.useSystemKiota.get()) {
            "kiota"
        } else {
            val versionFolder = extension.targetBinaryFolder.get().asFile.resolve(extension.kiotaVersion.get())
            downloadAndExtract(
                "${extension.baseURL.get()}/v${extension.kiotaVersion.get()}/${resolveArtifact()}.zip",
                versionFolder
            )
            versionFolder.resolve(resolveBinary()).absolutePath
        }

        val openApiSpec = resolveOpenApiSpec()
        executeKiota(executablePath, openApiSpec)
    }

    private fun resolveArtifact(): String {
        val os = when {
            extension.osName.get().startsWith("Linux", true) -> "linux"
            extension.osName.get().startsWith("Mac", true) -> "osx"
            extension.osName.get().startsWith("Win", true) -> "win"
            else -> throw IllegalArgumentException("Unsupported OS: ${extension.osName.get()}")
        }

        val arch = when {
            extension.osArch.get().contains("64") -> "x64"
            extension.osArch.get().contains("86") -> "x86"
            else -> throw IllegalArgumentException("Unsupported architecture: ${extension.osArch.get()}")
        }

        return "$os-$arch"
    }

    private fun resolveBinary(): String {
        return if (extension.osName.get().startsWith("Win", true)) "kiota.exe" else "kiota"
    }

    private fun resolveOpenApiSpec(): File {
        return when {
            extension.file.isPresent && extension.url.isPresent -> {
                throw IllegalArgumentException("Provide only one of 'file' or 'url'")
            }
            extension.file.isPresent -> extension.file.get().asFile
            extension.url.isPresent -> downloadSpec(extension.url.get())
            else -> throw IllegalArgumentException("Provide one of 'file' or 'url'")
        }
    }

    private fun downloadSpec(url: URL): File {
        val destDir = extension.downloadTarget.get().asFile
        destDir.mkdirs()
        val destination = File(destDir, File(url.path).name)
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
        val finalBinary = File(dest, resolveBinary())

        if (!finalBinary.exists()) {
            dest.mkdirs()
            URL(url).openStream().use { input ->
                zipFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            FileSystems.newFileSystem(zipFile.toPath(), emptyMap<String, Any?>()).use { fs ->
                Files.copy(fs.getPath("/${resolveBinary()}"), finalBinary.toPath())
            }
            finalBinary.setExecutable(true, false)
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
            "--output", extension.targetDirectory.get().asFile.absolutePath,
            "--language", extension.language.get(),
            "--class-name", extension.clientClass.get(),
            "--namespace-name", extension.namespace.get()
        )

        extension.serializers.get().forEach {
            cmd.addAll(listOf("--serializer", it))
        }

        extension.deserializers.get().forEach {
            cmd.addAll(listOf("--deserializer", it))
        }

        extension.includePath.get().forEach {
            cmd.addAll(listOf("--include-path", it))
        }

        extension.excludePath.get().forEach {
            cmd.addAll(listOf("--exclude-path", it))
        }

        if (extension.cleanOutput.get()) cmd.add("--clean-output")
        if (extension.clearCache.get()) cmd.add("--clear-cache")

        cmd.addAll(listOf("--log-level", extension.kiotaLogLevel.get()))

        val process = ProcessBuilder(cmd)
            .directory(extension.projectDir.get().asFile)
            .inheritIO()
            .start()

        if (!process.waitFor(extension.kiotaTimeout.get().toLong(), TimeUnit.SECONDS)) {
            throw RuntimeException("Kiota process timed out")
        }

        if (process.exitValue() != 0) {
            throw RuntimeException("Kiota process failed with exit code ${process.exitValue()}")
        }

        logger.lifecycle("✅ Kiota code generation completed")
    }
}