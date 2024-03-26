plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation("org.jetbrains.intellij.plugins", "gradle-intellij-plugin", "1.17.0")
  implementation("org.jetbrains.kotlin", "kotlin-gradle-plugin", "1.9.22")
}
