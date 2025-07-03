plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

group = "chase.jetbrains.plugin.rustrover"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        create("RR", "2025.1.4")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        // Add necessary plugin dependencies for compilation here, example:
//        bundledPlugin("com.intellij.java")
//        bundledPlugin("org.rust.lang")
//        bundledPlugin("com.intellij.rust")
//        localPlugin("com.jetbrains.rust")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
        }

        changeNotes = """
      <h3>1.0</h3>
      <ul>
        <li>Initial release of TAVERN Project Wizard</li>
        <li>Support for creating Tauri projects with customizable settings</li>
        <li>Support for creating Actix projects with API port configuration</li>
        <li>Comprehensive documentation and README</li>
      </ul>
    """.trimIndent()
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "21"
    }
}
