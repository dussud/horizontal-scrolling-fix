plugins {
    `maven-publish`

    id("dev.kikugie.loom-back-compat")
}

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = property("mod.id") as String

val requiredJava = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.6" -> JavaVersion.VERSION_21
    else -> JavaVersion.VERSION_17
}

val compatibleVersions: List<String> = sc.properties.rawOrNull("mod", "mc_releases")?.asList().orEmpty().map {it.toString()}

val ukulib = if (sc.current.parsed >= "26.1") "ukulib-fabric" else "ukulib"

repositories {
    /**
     * Restricts dependency search of the given [groups] to the [maven URL][url],
     * improving the setup speed.
     */
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }

    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
    strictMaven("https://maven.uku3lig.net/releases", "ukuRepoReleases", "net.uku3lig")
    strictMaven("https://maven.uku3lig.net/releases", "mclogs", "gs.mclo.java")

}

dependencies {
    /**
     * Fetches only the required Fabric API modules to not waste time downloading all of them for each version.
     * @see <a href="https://github.com/FabricMC/fabric">List of Fabric API modules</a>
     */
    fun fapi(vararg modules: String) {
        for (it in modules) modImplementation(fabricApi.module(it, sc.properties["deps.fabric_api"]))
    }

    minecraft("com.mojang:minecraft:${sc.current.version}")
    // Applies Mojang Mappings on obfuscated versions
    loomx.applyMojangMappings()

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")

//    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")

    modImplementation("net.uku3lig:${ukulib}:${property("deps.ukulib")}")
}

//loom {
//    splitEnvironmentSourceSets()
//
//    mods {
//        register("horizontal-scrolling-fix") {
//            sourceSet(sourceSets.main.get())
//            sourceSet(sourceSets.getByName("client"))
//        }
//    }
//}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("horizontal-scrolling-fix") {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.getByName("client"))
        }
    }

    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json") // Useful for interface injection
//    accessWidenerPath = sc.process(
//        rootProject.file("src/main/resources/horizontal-scrolling-fix.ct"),
//        "build/processed.ct"
//    )

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // Adds names to lambdas - useful for mixins
    }

    runConfigs.all {
        vmArgs("-Dmixin.debug.export=true") // Exports transformed classes for debugging
        runDir = "../../run" // Shares the run directory between versions
    }
}

java {
    withSourcesJar()

    sourceCompatibility = requiredJava
    targetCompatibility = requiredJava

//    toolchain {
//        vendor = JvmVendorSpec.ADOPTIUM
//        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
//    }
}

tasks {
    val mixinJava = "JAVA_${requiredJava.majorVersion}"

    processResources {
        fun MutableMap<String, String>.register(key: String, property: String) {
            val value: String = sc.properties[property]
            inputs.property(key, value)
            set(key, value)
        }

        val props = buildMap {
            register("id", "mod.id")
            register("name", "mod.name")
            register("version", "mod.version")
            register("minecraft", "mod.mc_compat")
            inputs.property("java_version", requiredJava.majorVersion)
            put("java_version", requiredJava.majorVersion)
        }

        filesMatching("fabric.mod.json") { expand(props) }

    }

    named<ProcessResources>("processClientResources") {
        inputs.property("java", mixinJava)

        filesMatching("*.mixins.json") { expand("java" to mixinJava) }
    }

    // Builds the version into a shared folder in `build/libs/${mod version}/`
    register<Copy>("buildAndCollect") {
        group = "build"

        // loomx.mod(Sources)Jar returns the jar task for the applied loom variant
        from(loomx.modJar.map { it.archiveFile }, loomx.modSourcesJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

//tasks.processResources {
//    val version = version
//    inputs.property("version", version)
//
//    filesMatching("fabric.mod.json") {
//        expand("version" to version)
//    }
//}
//
//tasks.withType<JavaCompile>().configureEach {
//    options.release = 21
//}
//
//tasks.jar {
//    val projectName = project.name
//    inputs.property("projectName", projectName)
//
//    from("LICENSE") {
//        rename { "${it}_$projectName" }
//    }
//}
//
//// configure the maven publication
//publishing {
//    publications {
//        register<MavenPublication>("mavenJava") {
//            from(components["java"])
//        }
//    }
//
//    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
//    repositories {
//        // Add repositories to publish to here.
//        // Notice: This block does NOT have the same function as the block in the top level.
//        // The repositories here will be used for publishing your artifact, not for
//        // retrieving dependencies.
//    }
//}
