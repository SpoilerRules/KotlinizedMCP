plugins {
    kotlin("jvm") version "1.9.21"
}

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net/")
    maven("https://repo.marcloud.net/")
    maven("https://jitpack.io")
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3") {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-common")
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    } // Latest as of November 06 (2023)

    // Netty
    implementation("io.netty:netty-buffer:4.1.100.Final") // Latest as of October 25 (2023)
    implementation("io.netty:netty-codec:4.1.100.Final") // Latest as of October 25 (2023)
    implementation("io.netty:netty-common:4.1.100.Final") // Latest as of October 25 (2023)
    implementation("io.netty:netty-handler:4.1.100.Final") // Latest as of October 25 (2023)
    implementation("io.netty:netty-resolver:4.1.100.Final") // Latest as of October 25 (2023)
    implementation("io.netty:netty-transport:4.1.100.Final") // Latest as of October 25 (2023)
    implementation("io.netty:netty-transport-classes-epoll:4.1.100.Final") // Latest as of October 25 (2023)
    implementation("io.netty:netty-transport-native-epoll:4.1.100.Final") // Latest as of October 25 (2023)
    implementation("io.netty:netty-transport-native-unix-common:4.1.100.Final") // Latest as of October 25 (2023)

    // System related
    implementation("com.github.oshi:oshi-core:6.4.6") {
        exclude("net.java.dev.jna", "jna-platform")
        exclude("net.java.dev.jna", "jna")
        exclude("org.slf4j", "slf4j-api")
    } // Latest as of October 25 (2023)

    // Command line
    implementation("net.sf.jopt-simple:jopt-simple:5.0.4") // Latest as of 2023 August 22

    // LWJGL
    /* Uncomment this when you are ready to start utilizing LWJGL 3.
    * api("org.lwjgl:lwjgl:3.3.2") // Latest as of 2023 August 23
    * api("org.lwjgl:lwjgl-opengl:3.3.2") // Latest as of 2023 August 22
    * api("org.lwjgl:lwjgl-glfw:3.3.2") // Latest as of 2023 August 22
    * api("org.lwjgl:lwjgl-openal:3.3.2") // Latest as of 2023 August 22
    */

    api("org.lwjgl:lwjgl:2.9.4-nightly") // Latest as of 2023 September 04
    api("org.lwjgl:util:2.9.4-nightly") // Latest as of 2023 September 04

    // Apache HttpComponents
    implementation("org.apache.httpcomponents:httpclient:4.5.14") // Latest as of 2023 August 22
    implementation("org.apache.httpcomponents:httpcore:4.4.16") // Latest as of 2023 August 22

    // JNA (Java Native Access)
    implementation("net.java.dev.jna:jna:5.13.0") // Latest as of 2023 August 22
    implementation("net.java.dev.jna:jna-platform:5.13.0") {
        exclude("net.java.dev.jna", "jna")
    } // Latest as of 2023 August 22

    // Logging
    implementation("org.apache.logging.log4j:log4j-api:2.21.1") // Latest as of November 14 (2023)
    implementation("org.apache.logging.log4j:log4j-core:2.21.1") // Latest as of November 14 (2023)
    runtimeOnly("org.slf4j:slf4j-api:2.0.9") // Latest as of November 08 (2023)
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j2-impl:2.21.1") {
        exclude("org.apache.logging.log4j")
        exclude("org.slf4j")
    } // Latest as of November 14
    runtimeOnly("com.mojang:logging:1.1.1") {
        exclude("org.apache.logging.log4j")
        exclude("org.slf4j")
    } // Latest as of November 14 (2023)

    // Miscellaneous
    implementation("org.apache.commons:commons-compress:1.23.0") // Latest as of October 25 (2023)
    implementation("org.apache.commons:commons-lang3:3.13.0") // Latest as of 2023 August 22
    implementation("org.apache.commons:commons-text:1.10.0") // Latest as of 2023 September 22
    implementation("commons-io:commons-io:2.14.0") // Latest as of October 25 (2023)
    implementation("commons-codec:commons-codec:1.16.0") // Latest as of 2023 August 22
    api("com.google.guava:guava:32.1.3-jre") // Latest as of October 25 (2023)
    api("com.google.code.gson:gson:2.10.1") // Latest as of 2023 August 21

    // Sound Libraries (Pending removal by implementation of LWJGL 3.)
    implementation("com.paulscode.sound:libraryjavasound:20101123")
    implementation("com.paulscode.sound:codecwav:20101023")
    implementation("com.paulscode.sound:soundsystem:20120107")
    implementation("com.paulscode.sound:codecjorbis:20101023")
    implementation("com.paulscode.sound:librarylwjglopenal:20100824")

    // Mojang
    implementation("com.ibm.icu:icu4j:icu4j-70.1") // Neutral
    api("com.mojang:authlib:3.11.50")

    // Microsoft account support
    implementation("com.github.CCBlueX:Elixir:1.2.6") {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib")
        exclude("com.mojang", "authlib")
    }

    // Testing (useless for now)
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.21")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

configurations.all {
    resolutionStrategy {
        eachDependency {
            if (requested.group == "com.ibm.icu" && requested.name == "icu4j") {
                useVersion("70.1")
                because("Force the usage of JAR instead of POM for icu4j dependency.")
            }
        }
    }
}

sourceSets {
    getByName("main") {
        kotlin {
            srcDir("src/main/kotlin")
            srcDir("evanescent/kotlin")
        }
        java {
            srcDir("src/main/java")
            srcDir("evanescent/java")
        }
    }
}

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

tasks.jar{
//  ↓ To change the name of the JAR file, modify the string below. ←
//                         ↓ ↓ ↓
    archiveBaseName.set("Evanescent")

    duplicatesStrategy = DuplicatesStrategy.FAIL
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
   // options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
    options.compilerArgs.add("-Xlint:deprecation")
}

tasks.withType<JavaExec> {
    val osType = System.getProperty("os.name").lowercase().let {
        if (it.contains("windows")) "windows" else "linux"
    }
    systemProperty("java.library.path", "${projectDir}${File.separator}test_natives${File.separator}$osType")
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
