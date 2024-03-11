fun properties(key: String) = project.findProperty(key).toString()

plugins {
  checkstyle
  id("java")
  id("idea")
  id("org.jetbrains.intellij")
}

version = properties("pluginVersion")

repositories {
  mavenCentral()
}

intellij {
  type.set(properties("platformType"))
  version.set(properties("platformVersion"))
}

checkstyle {
  toolVersion = "10.12.5"
}

dependencies {
  implementation("ee.carlrobert:llm-client:0.6.1")

  testImplementation("org.assertj:assertj-core:3.25.3")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
  testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.10.2")
}

tasks {
  properties("javaVersion").let {
    withType<JavaCompile> {
      sourceCompatibility = it
      targetCompatibility = it
    }
  }

  jar {
    archiveBaseName.set(properties("pluginName") + "-" + project.name)
  }

  runIde {
    enabled = false
  }

  verifyPlugin {
    enabled = false
  }

  runPluginVerifier {
    enabled = false
  }

  patchPluginXml {
    enabled = false
  }

  publishPlugin {
    enabled = false
  }

  signPlugin {
    enabled = false
  }
}