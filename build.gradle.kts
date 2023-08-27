import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
  id("codegpt.java-conventions")
  id("org.jetbrains.changelog") version "2.1.2"
  id("org.jetbrains.grammarkit") version "2021.2.2"
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
  implementation(project("embeddings", "instrumentedJar"))
  implementation(project(mapOf("path" to ":embeddings")))

  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.14.2")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")
  implementation("com.vladsch.flexmark:flexmark-all:0.64.8") {
    // vulnerable transitive dependency
    exclude(group = "org.jsoup", module = "jsoup")
  }
  implementation("org.jsoup:jsoup:1.16.1")
  implementation("org.apache.commons:commons-text:1.10.0")
  implementation("com.knuddels:jtokkit:0.6.1")
  implementation("org.quartz-scheduler:quartz:2.3.2")

  testImplementation("org.assertj:assertj-core:3.24.2")
  testImplementation("org.awaitility:awaitility:4.2.0")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.6.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.1")
  testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.6.1")
}

tasks {
  wrapper {
    gradleVersion = properties("gradleVersion").get()
  }

  runIde {
    enabled = true
  }

  verifyPlugin {
    enabled = true
  }

  runPluginVerifier {
    enabled = true
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