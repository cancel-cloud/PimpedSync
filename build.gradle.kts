import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20-M1"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "de.cancelcloud"
version = "1.2"

val exposedVersion: String by project
var host = "github.com/cancel-cloud/PimpedSync"

repositories {
    mavenCentral()
    maven("https://repo.purpurmc.org/snapshots")
}

dependencies {
    //Purpur aka Minecraft
    compileOnly("org.purpurmc.purpur", "purpur-api", "1.18.2-R0.1-SNAPSHOT")

    //Kotlin Stuff
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2") // Serialization
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0") // Coroutines

    //Jetbrains-Exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation("org.postgresql:postgresql:42.3.3")


    //JET
    compileOnly("com.github.TheFruxz.MoltenKT:moltenkt-core:1.0-PRE-7a") // MoltenKT-Core
    compileOnly("com.github.TheFruxz.MoltenKT:moltenkt-paper:1.0-PRE-7a") // MoltenKT-Paper
    compileOnly("com.github.TheFruxz.MoltenKT:moltenkt-unfold:1.0-PRE-7a") // MoltenKT-Unfold
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.processResources {
    expand("version" to project.version, "name" to project.name, "website" to "https://$host")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveClassifier.set("Runnable")
    dependencies {
        include(dependency("org.postgresql:postgresql:42.3.3"))
    }
}