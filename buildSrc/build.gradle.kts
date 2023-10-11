plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation("org.jetbrains.intellij.plugins", "gradle-intellij-plugin", "1.16.0")
}
