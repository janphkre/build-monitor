package de.janphkre.buildmonitor.util

enum class ResourceFile(val fileName: String) {
    SERVER_HELLO_WORLD("serverHelloWorld.gradle"),
    HELLO_WORLD("helloWorld.gradle"),
    FAILING("failing.gradle"),
    DEPENDENCIES("dependencies.gradle"),
    GRAMMAR_ERROR("grammarError.gradle"),
    JAVA_BUILD_GRADLE("javaBuild/build.gradle"),
    JAVA_BUILD_CLASS("javaBuild/Something.java")
}