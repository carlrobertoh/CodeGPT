plugins {
  id("java")
  id("org.jetbrains.intellij") version "1.5.2"
}

group = "ee.carlrobert"
version = "1.0.7"

repositories {
  mavenCentral()
}

intellij {
  version.set("2021.2")
  type.set("IC")
  plugins.set(listOf())
}

tasks {
  withType<JavaCompile> {
    sourceCompatibility = "11"
    targetCompatibility = "11"
  }

  patchPluginXml {
    sinceBuild.set("212")
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
