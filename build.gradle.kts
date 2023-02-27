plugins {
  id("java")
  id("org.jetbrains.intellij") version "1.12.0"
}

group = "ee.carlrobert"
version = "1.1.1"

repositories {
  mavenCentral()
}

intellij {
  version.set("2022.2")
  type.set("IC")
  plugins.set(listOf())
}

dependencies {
  implementation("com.fifesoft:rsyntaxtextarea:3.3.2")
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

tasks {
  patchPluginXml {
    sinceBuild.set("222.0")
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
