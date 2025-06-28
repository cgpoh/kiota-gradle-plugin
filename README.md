# Kiota Gradle Plugin

The Kiota Gradle plugin eases the usage of the Kiota CLI from Gradle projects. To use the plugin, apply this plugin in your `build.gradle`:

```groovy
plugins {
    id "io.github.cgpoh.kiota-gradle-plugin" version "${version}"
}
```

The plugin provides a task `kiotaGenerate` that can be used to generate client code from OpenAPI specifications. You can configure the task in your `build.gradle`:

```groovy
kiota {
    kiotaVersion.set("1.24.0")
    file.set(file("../openapi/open-api.json"))
    targetDirectory.set(file("src/main/java/test/client"))
    language.set("Java")
    clientClass.set("TestClient")
    namespace.set("test.client")
}
```

```properties
Available parameters:

    baseURL (Default: https://github.com/microsoft/kiota/releases/download)
      Base URL to be used for the download

    cleanOutput (Default: false)
      Clean output before generating

    clearCache (Default: false)
      Clear cache before generating

    clientClass (Default: ApiClient)
      The class name to use for the core client class. [default: ApiClient]

    deserializers (Default:
    io.kiota.serialization.json.JsonParseNodeFactory,com.microsoft.kiota.serialization.TextParseNodeFactory)
      The deserializers to be used by Kiota

    downloadTarget (Default: build/openapi-spec)
      The Download target folder for API spec downloaded from remote URLs

    file
      The openapi specification to be used for generating code

    kiotaLogLevel (Default: Warning)
      The log level of Kiota to use when logging messages to the main output.
      [default: Warning]

    kiotaTimeout (Default: 30)
      Kiota timeout in seconds

    kiotaVersion (Default: 1.24.0)
      Version of Kiota to be used

    language (Default: Java)
      Language to generate the code for

    namespace (Default: com.apisdk)
      The namespace to use for the core client class

    osName (Default: ${os.name})
      OS name

    serializers (Default:
    io.kiota.serialization.json.JsonParseNodeFactory,com.microsoft.kiota.serialization.TextParseNodeFactory)
      The serializers to be used by Kiota

    skip (Default: false)
      Skip the execution of the goal

    targetBinaryFolder (Default:
    ${project.build.directory}/kiota/)
      Required: true
      Kiota executable target binary folder

    targetDirectory (Default:
    build/generated-sources/kiota)
      Location where to generate the Java code

    url
      The URL to be used to download an API spec from a remote location

    useSystemKiota (Default: false)
      Use system provided kiota executable (needs to be available on the PATH)
```