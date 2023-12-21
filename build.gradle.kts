import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import java.io.FileInputStream
import java.util.*

val env = environment("env").getOrNull()

fun loadProperties(filename: String): Properties = Properties().apply {
    load(FileInputStream(filename))
}

fun properties(key: String): Provider<String> {
  if ("win-arm64" == env) {
    val property = loadProperties("gradle-win-arm64.properties").getProperty(key)
            ?: return providers.gradleProperty(key)
    return providers.provider { property }
  }
  return providers.gradleProperty(key)
}

fun environment(key: String) = providers.environmentVariable(key)

plugins {
  id("codegpt.java-conventions")
  id("org.jetbrains.changelog") version "2.2.0"
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

repositories {
  mavenCentral()
  gradlePluginPortal()
}

intellij {
  pluginName.set(properties("pluginName"))
  version.set(properties("platformVersion"))
  type.set(properties("platformType"))
  plugins.set(listOf("java"))
}

changelog {
  groups.empty()
  repositoryUrl.set(properties("pluginRepositoryUrl"))
}

dependencies {
  implementation(project(":codegpt-core"))
  implementation(project(":codegpt-telemetry"))

  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.16.0")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0")
  implementation("com.vladsch.flexmark:flexmark-all:0.64.8") {
    // vulnerable transitive dependency
    exclude(group = "org.jsoup", module = "jsoup")
  }
  implementation("org.jsoup:jsoup:1.17.1")
  implementation("org.apache.commons:commons-text:1.11.0")
  implementation("com.knuddels:jtokkit:0.6.1")

  testImplementation("org.assertj:assertj-core:3.24.2")
  testImplementation("org.awaitility:awaitility:4.2.0")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.1")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
  testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.10.1")
}

tasks.register<Exec>("updateSubmodules") {
  workingDir(rootDir)
  commandLine("git", "submodule", "update", "--init", "--recursive")
}

tasks.register<Copy>("copyLlamaSubmodule") {
  dependsOn("updateSubmodules")
  from(layout.projectDirectory.file("src/main/cpp/llama.cpp"))
  into(layout.buildDirectory.dir("idea-sandbox/plugins/CodeGPT/llama.cpp"))
}

tasks {
  wrapper {
    gradleVersion = properties("gradleVersion").get()
  }

  verifyPlugin {
    enabled = true
  }

  runPluginVerifier {
    enabled = true
  }

  patchPluginXml {
    enabled = true
    version.set(properties("pluginVersion"))
    sinceBuild.set(properties("pluginSinceBuild"))
    untilBuild.set(properties("pluginUntilBuild"))

    pluginDescription.set(providers.fileContents(layout.projectDirectory.file("DESCRIPTION.md")).asText.map {
      val start = "<!-- Plugin description -->"
      val end = "<!-- Plugin description end -->"

      with(it.lines()) {
        if (!containsAll(listOf(start, end))) {
          throw GradleException("Plugin description section not found in DESCRIPTION.md:\n$start ... $end")
        }
        subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
      }
    })

    val changelog = project.changelog // local variable for configuration cache compatibility
    // Get the latest available change notes from the changelog file
    changeNotes.set(properties("pluginVersion").map { pluginVersion ->
      with(changelog) {
        renderItem(
          (getOrNull(pluginVersion) ?: getUnreleased())
            .withHeader(false)
            .withEmptySections(false),
          Changelog.OutputType.HTML,
        )
      }
    })
  }

  prepareSandbox {
    enabled = true
    dependsOn("copyLlamaSubmodule")
  }

  signPlugin {
    enabled = true
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  buildPlugin {
    enabled = true
  }

  publishPlugin {
    enabled = true
    dependsOn("patchChangelog")
    token.set(System.getenv("PUBLISH_TOKEN"))
    channels.set(listOf("stable"))
  }

  runIde {
    enabled = true
    environment("ENVIRONMENT", "LOCAL")
  }

  test {
    exclude("**/testsupport/*")
    useJUnitPlatform()
    testLogging {
      events("started", "passed", "skipped", "failed")
      exceptionFormat = TestExceptionFormat.FULL
      showStandardStreams = true
    }
  }
}