plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation(libs.gradle.intellij.plugin) {
    // vulnerable transitive dependency okhttp 3.14.9 in gradle-intellij-plugin 1.17.3
    exclude(group = "com.squareup.okhttp3", module = "okhttp")
  }
  implementation(libs.kotlin.gradle.plugin)
}
