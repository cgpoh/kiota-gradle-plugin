package io.kiota.gradle.plugin

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.file.DirectoryProperty
import java.io.File
import javax.inject.Inject
import java.net.URL

abstract class KiotaExtension @Inject constructor(objects: ObjectFactory) {
    val skip: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    val useSystemKiota: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

    val targetBinaryFolder: DirectoryProperty = objects.directoryProperty().convention(
        objects.directoryProperty().fileValue(File("build/kiota"))
    )

    val osName: Property<String> = objects.property(String::class.java).convention(System.getProperty("os.name"))
    val osArch: Property<String> = objects.property(String::class.java).convention(System.getProperty("os.arch"))
    val baseURL: Property<String> = objects.property(String::class.java).convention("https://github.com/microsoft/kiota/releases/download")
    val kiotaVersion: Property<String> = objects.property(String::class.java).convention("1.22.2")

    val file: RegularFileProperty = objects.fileProperty()
    val url: Property<URL> = objects.property(URL::class.java)

    val downloadTarget: DirectoryProperty = objects.directoryProperty().convention(
        objects.directoryProperty().fileValue(File("build/openapi-spec"))
    )

    val targetDirectory: DirectoryProperty = objects.directoryProperty().convention(
        objects.directoryProperty().fileValue(File("build/generated-sources/kiota"))
    )

    val serializers: ListProperty<String> = objects.listProperty(String::class.java).convention(
        listOf(
            "io.kiota.serialization.json.JsonSerializationWriterFactory",
            "com.microsoft.kiota.serialization.TextSerializationWriterFactory"
        )
    )

    val deserializers: ListProperty<String> = objects.listProperty(String::class.java).convention(
        listOf(
            "io.kiota.serialization.json.JsonParseNodeFactory",
            "com.microsoft.kiota.serialization.TextParseNodeFactory"
        )
    )

    val includePath: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())
    val excludePath: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

    val language: Property<String> = objects.property(String::class.java).convention("Java")
    val clientClass: Property<String> = objects.property(String::class.java).convention("ApiClient")
    val namespace: Property<String> = objects.property(String::class.java).convention("com.apisdk")

    val cleanOutput: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    val clearCache: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

    val kiotaTimeout: Property<Int> = objects.property(Int::class.java).convention(30)
    val kiotaLogLevel: Property<String> = objects.property(String::class.java).convention("Warning")

    val projectDir: DirectoryProperty = objects.directoryProperty()
}