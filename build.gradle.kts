plugins {
  id("java")
  id("org.jetbrains.intellij") version "1.13.1"
}

group = "ee.carlrobert"
version = "1.4.2"

repositories {
  mavenCentral()
}

intellij {
  version.set("2021.1")
  type.set("IC")
  plugins.set(listOf())
}

dependencies {
  implementation("com.fifesoft:rsyntaxtextarea:3.3.2")
  implementation("com.squareup.okhttp3:okhttp:4.10.0")
  implementation("com.squareup.okhttp3:okhttp-sse:4.10.0")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.14.2")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

tasks {
  patchPluginXml {
    sinceBuild.set("211")
    untilBuild.set("231.*")
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }
}
