plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation(libs.gradle.intellij.plugin)
  implementation(libs.kotlin.gradle.plugin)
}
