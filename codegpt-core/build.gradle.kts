plugins {
    id("codegpt.java-conventions")
    id("antlr")
}

dependencies {
    antlr("org.antlr:antlr4:4.13.1")
    api("com.github.jelmerk:hnswlib-core:1.1.0")
    api("com.github.jelmerk:hnswlib-utils:1.1.0")
    implementation("org.antlr:antlr4-runtime:4.13.1")
    implementation("org.json:json:20231013")
}

tasks {
    generateGrammarSource {
        outputDirectory = file("src/main/java/grammar")
        arguments = arguments + listOf("-package", "grammar")
    }
}