package io.kiota.gradle.plugin

import java.io.File
import java.net.URL

open class KiotaExtension {
    var skip: Boolean = false
    var useSystemKiota: Boolean = false
    var targetBinaryFolder: File = File("build/kiota")
    var osName: String = System.getProperty("os.name")
    var osArch: String = System.getProperty("os.arch")
    var baseURL: String = "https://github.com/microsoft/kiota/releases/download"
    var kiotaVersion: String = "1.22.2"
    var file: File? = null
    var url: URL? = null
    var downloadTarget: File = File("build/openapi-spec")
    var targetDirectory: File = File("build/generated-sources/kiota")
    var serializers: List<String> = listOf(
        "io.kiota.serialization.json.JsonSerializationWriterFactory",
        "com.microsoft.kiota.serialization.TextSerializationWriterFactory"
    )
    var deserializers: List<String> = listOf(
        "io.kiota.serialization.json.JsonParseNodeFactory",
        "com.microsoft.kiota.serialization.TextParseNodeFactory"
    )
    var includePath: List<String>? = null
    var excludePath: List<String>? = null
    var language: String = "Java"
    var clientClass: String = "ApiClient"
    var namespace: String = "com.apisdk"
    var cleanOutput: Boolean = false
    var clearCache: Boolean = false
    var kiotaTimeout: Int = 30
    var kiotaLogLevel: String = "Warning"
}