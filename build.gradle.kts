import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    `maven-publish`
}

val libraryVersion = "1.0.0"

group = "dev.minjae.pnx.kommand"
version = libraryVersion

repositories {
    mavenCentral()
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
    maven {
        name = "opencollab-repo-release"
        url = uri("https://repo.opencollab.dev/maven-releases")
    }
    maven {
        name = "opencollab-repo-snapshot"
        url = uri("https://repo.opencollab.dev/maven-snapshots")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("cn.powernukkitx:powernukkitx:1.20.0-r1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val javadoc: Javadoc by tasks

val sourcesJar = task<Jar>("sourcesJar") {
    from(sourceSets["main"].allSource)
    archiveClassifier.set("sources")
}

val javadocJar = task<Jar>("javadocJar") {
    from(javadoc.destinationDir)
    archiveClassifier.set("javadoc")

    dependsOn(javadoc)
}

tasks {
    build {
        dependsOn(javadocJar)
        dependsOn(sourcesJar)
        dependsOn(jar)
    }
    test {
        useJUnitPlatform()
    }
}

// setup maven central publishing

publishing.publications {
    register<MavenPublication>("Release") {
        from(components["java"])
        groupId = project.group as String
        artifactId = project.name
        version = project.version as String

        artifact(javadocJar)
        artifact(sourcesJar)
    }
}
