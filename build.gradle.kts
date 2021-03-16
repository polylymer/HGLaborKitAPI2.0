import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/*
 * BUILD CONSTANTS
 */

val GITHUB_REPO = "HGLabor/HGLaborKitAPI2.0"

val JVM_VERSION = JavaVersion.VERSION_11
val JVM_VERSION_STRING = JVM_VERSION.versionString

/*
 * PROJECT
 */

group = "de.hglabor"
version = "0.2.0"

description = "KitAPI for HGLabor"

/*
 * PLUGINS
 */

plugins {
    java
    kotlin("jvm") version "1.4.30"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    `maven-publish`
    signing
}

/*
 * DEPENDENCY MANAGEMENT
 */

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.md-5.net/content/groups/public/")
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://jitpack.io/")

    // COMMAND API
    maven("https://raw.githubusercontent.com/JorelAli/CommandAPI/mvn-repo/")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    compileOnly("org.apache.commons", "commons-lang3", "3.11")
    compileOnly("LibsDisguises", "LibsDisguises", "10.0.21")
    compileOnly("com.destroystokyo.paper", "paper-api", "1.16.5-R0.1-SNAPSHOT")
    compileOnly("org.bukkit", "craftbukkit", "1.16.5-R0.1-SNAPSHOT")
    compileOnly("de.hglabor", "localization", "0.0.5")
    compileOnly("de.hglabor", "hglabor-utils", "0.0.4")
    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.2.0-SNAPSHOT")
    compileOnly("dev.jorel", "commandapi-shade", "5.8")
}

/*
 * BUILD
 */

java.targetCompatibility = JVM_VERSION
java.sourceCompatibility = JVM_VERSION

tasks {
    compileKotlin.configureJvmVersion()
    compileTestKotlin.configureJvmVersion()
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

/*
 * PUBLISHING
 */

publishing {

    repositories {
        maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
            credentials {
                username = if (hasProperty("ossrhUsername")) property("ossrhUsername") as String else "NAME"
                password = if (hasProperty("ossrhPassword")) property("ossrhPassword") as String else "PASSWORD"
            }
        }

        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
            credentials {
                username = if (hasProperty("ossrhUsername")) property("ossrhUsername") as String else "NAME"
                password = if (hasProperty("ossrhPassword")) property("ossrhPassword") as String else "PASSWORD"
            }
        }
    }

    publications {
        create<MavenPublication>(project.name) {

            from(components["java"])

            this.groupId = project.group.toString()
            this.artifactId = project.name.toLowerCase()
            this.version = project.version.toString()

            pom {

                name.set(project.name)
                description.set(project.description)
                packaging = "jar"

                developers {
                    developer {
                        name.set("copyandexecute")
                    }
                }

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                url.set("https://github.com/${GITHUB_REPO}")

                scm {
                    connection.set("scm:git:git://github.com/${GITHUB_REPO}.git")
                    developerConnection.set("scm:git:ssh://github.com:${GITHUB_REPO}.git")
                    url.set("https://github.com/${GITHUB_REPO}/tree/main")
                }

            }

        }
    }

}

signing {
    sign(publishing.publications)
}

/*
 * EXTENSIONS
 */

val JavaVersion.versionString get() = majorVersion.let {
    val version = it.toInt()
    if (version <= 10) "1.$it" else it
}

fun TaskProvider<KotlinCompile>.configureJvmVersion() { get().kotlinOptions.jvmTarget = JVM_VERSION_STRING }