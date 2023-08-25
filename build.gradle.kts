import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
  id("java")
  id("antlr")
  id("org.jetbrains.intellij") version "1.15.0"
  id("org.jetbrains.kotlin.jvm") version "1.8.21"
  id("org.jetbrains.changelog") version "2.1.0"
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
  plugins.set(listOf())
}

changelog {
  groups.empty()
  repositoryUrl.set(properties("pluginRepositoryUrl"))
}

dependencies {
  antlr("org.antlr:antlr4:4.5")
  implementation("org.antlr:antlr4-runtime:4.7.1")

  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.14.2")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")
  implementation("com.vladsch.flexmark:flexmark-all:0.64.0") {
    // vulnerable transitive dependency
    exclude(group = "org.jsoup", module = "jsoup")
  }
  implementation("org.jsoup:jsoup:1.16.1")
  implementation("org.apache.commons:commons-text:1.10.0")
  implementation("ee.carlrobert:openai-client:1.2.0")
  implementation("com.knuddels:jtokkit:0.2.0")
  implementation("io.socket:socket.io-client:2.1.0") {
    // vulnerable transitive dependency
    exclude(group = "org.json", module = "json")
  }
  implementation("org.json:json:20230618")
  implementation("org.quartz-scheduler:quartz:2.3.2")
  implementation("com.github.jelmerk:hnswlib-core:1.1.0")
  implementation("com.github.jelmerk:hnswlib-utils:1.1.0")

  testImplementation("org.assertj:assertj-core:3.24.2")
  testImplementation("org.awaitility:awaitility:4.2.0")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.6.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.1")
  testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.6.1")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

tasks {
  wrapper {
    gradleVersion = properties("gradleVersion").get()
  }

  generateGrammarSource {
    outputDirectory = file("src/main/java/grammar")
    arguments = arguments + listOf("-package", "grammar")
  }

  patchPluginXml {
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

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    dependsOn("patchChangelog")
    token.set(System.getenv("PUBLISH_TOKEN"))
    channels.set(listOf("stable"))
  }

  runIde {
    environment("ENVIRONMENT", "LOCAL")
  }

  test {
    useJUnitPlatform()
    testLogging {
      events("passed", "skipped", "failed")
      exceptionFormat = TestExceptionFormat.FULL
      showStandardStreams = true
    }
  }
}