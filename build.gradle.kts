plugins {
    base
}

group = "com.cheatsheet"
version = "0.0.1-SNAPSHOT"

allprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
    }
}

subprojects {
    apply(plugin = "java")

    extensions.configure<org.gradle.api.plugins.JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

// Совместимость с прежними командами в корне проекта.
tasks.register("compileJava") {
    dependsOn(":quiz-app:compileJava")
}

tasks.register("bootRun") {
    dependsOn(":quiz-app:bootRun")
}

tasks.register("bootJar") {
    dependsOn(":quiz-app:bootJar")
}

tasks.register("javadoc") {
    dependsOn(":quiz-app:javadoc")
}
