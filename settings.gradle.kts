pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
    }

//    plugins {
//        id("net.fabricmc.fabric-loom-remap") version providers.gradleProperty("loom_version")
//    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.5"
    id("dev.kikugie.loom-back-compat") version "0.3"
}

stonecutter {
    create(rootProject) {
        versions("1.20.2", "1.20.3", "1.20.4", "1.20.6", "1.21", "1.21.2", "1.21.4", "1.21.5", "1.21.6", "1.21.9", "1.21.11", "26.1.2", "26.2", "26.3")
        vcsVersion = "1.21.11"
    }
}

// Should match your modid
rootProject.name = "horizontal-scrolling-fix"
