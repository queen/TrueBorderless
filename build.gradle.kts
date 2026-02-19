import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask
import dev.architectury.pack200.java.Pack200Adapter
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.utils.extendsFrom

val mod_id: String by project
val mod_version: String by project
val mod_name: String by project

plugins {
    kotlin("jvm") version "2.3.10"
    id("com.gradleup.shadow") version "9.3.1"
    id("gg.essential.loom") version "1.15.45"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("net.kyori.blossom") version "2.2.0"
    idea
}

loom {
    silentMojangMappingsLicense()
    runConfigs {
        getByName("client") {
            property("mixin.debug", "true")
            property("asmhelper.verbose", "true")
            programArgs("--tweakClass", "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker")
            programArgs("--mixin", "mixins.$mod_id.json")
            isIdeConfigGenerated = true
        }
    }
    forge {
        pack200Provider.set(Pack200Adapter())
        mixinConfig("mixins.$mod_id.json")
        accessTransformer("src/main/resources/${mod_id}_at.cfg")
    }
    mixin {
        useLegacyMixinAp = true
        defaultRefmapName = "mixins.$mod_id.refmap.json"
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
    maven("https://repo.spongepowered.org/maven/")
    maven("https://repo.polyfrost.cc/releases")
}

val shadowMe: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    shadowMe("net.java.dev.jna:jna:5.18.1")
    shadowMe("net.java.dev.jna:jna-platform:5.18.1")
    implementation(kotlin("stdlib-jdk8"))

    // :frowning2:
    modCompileOnly("cc.polyfrost:oneconfig-1.8.9-forge:0.2.2-alpha223")
    shadowMe("cc.polyfrost:oneconfig-wrapper-launchwrapper:1.0.0-beta17")

    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    compileOnly("org.spongepowered:mixin:0.8.5")
}

sourceSets {
    main {
        blossom {
            resources {
                property("version", mod_version)
            }
        }
        output.setResourcesDir(layout.buildDirectory.dir("classes/kotlin/main"))
    }
}

tasks {
    processResources {
        inputs.property("version", mod_version)
        inputs.property("mcversion", "1.8.9")

        filesMatching("mcmod.info") {
            expand(mapOf("version" to mod_version, "mcversion" to "1.8.9"))
        }

        dependsOn(compileJava)
    }
    named<RemapJarTask>("remapJar") {
        archiveBaseName.set(mod_name)
        input.set(shadowJar.flatMap { it.archiveFile })
    }
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set(mod_name)
        archiveClassifier.set("dev")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        configurations = listOf(shadowMe)
        relocate("com.llamalad7.mixinextras", "dev.queen.mixinextras")

        exclude(
            "**/LICENSE.md",
            "**/LICENSE.txt",
            "**/LICENSE",
            "**/NOTICE",
            "**/NOTICE.txt",
            "pack.mcmeta",
            "dummyThing",
            "**/module-info.class",
            "META-INF/proguard/**",
            "META-INF/maven/**",
            "META-INF/versions/**",
            "META-INF/com.android.tools/**",
            "fabric.mod.json",
            "/**/META-INF/services/io.ktor.serialization.kotlinx.KotlinxSerializationExtensionProvider",
//            "org/spongepowered/**"
        )
    }
    named<Jar>("jar") {
        manifest {
            attributes(
                mapOf(
                    "ModSide" to "CLIENT",
                    "ForceLoadAsMod" to true,
                    "TweakClass" to "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker",
                    "TweakOrder" to 0,
                    "MixinConfigs" to "mixins.$mod_id.json",
                    "FMLAT" to "${mod_name}_at.cfg"
                )
            )
        }
        dependsOn(shadowJar)
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

kotlin.jvmToolchain(8)
java.toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
}