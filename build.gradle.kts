plugins {
    kotlin("jvm") version "1.9.20-RC"
}

repositories {
    mavenCentral()
    maven("https://repo.marcloud.net/")
    maven("https://jitpack.io")
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.20-Beta2")

    // Netty
    implementation("io.netty:netty-handler:4.1.98.Final")
    implementation("io.netty:netty-buffer:4.1.98.Final")
    implementation("io.netty:netty-transport-native-epoll:4.1.98.Final")
    implementation("io.netty:netty-transport-classes-epoll:4.1.99.Final") // Latest as of October 1
    implementation("io.netty:netty-common:4.1.98.Final")
    implementation("io.netty:netty-codec:4.1.98.Final")
    implementation("io.netty:netty-transport:4.1.98.Final")
    implementation("io.netty:netty-resolver:4.1.98.Final")
    implementation("io.netty:netty-transport-native-unix-common:4.1.99.Final") // Latest as of October 1

    // System related
    implementation("com.github.oshi:oshi-core:6.4.5") // Latest as of 2023 August 24

    // Command line
    implementation("net.sf.jopt-simple:jopt-simple:5.0.4") // Latest as of 2023 August 22

    // LWJGL
    /** Uncomment this when you are ready to start utilizing LWJGL 3.
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
    implementation("net.java.dev.jna:jna-platform:5.13.0") // Latest as of 2023 August 22

    // Logging
    implementation("org.apache.commons:commons-compress:1.23.0") // Latest as of 2023 August 22
    implementation("org.apache.logging.log4j:log4j-api:2.20.0") // Latest as of 2023 August 22
    implementation("org.apache.logging.log4j:log4j-core:2.20.0") // Latest as of 2023 August 22

    // Miscellaneous
    implementation("org.apache.commons:commons-lang3:3.13.0") // Latest as of 2023 August 22
    implementation("org.apache.commons:commons-text:1.10.0") // Latest as of 2023 September 22
    implementation("commons-io:commons-io:2.13.0") // Latest as of 2023 August 22
    implementation("commons-codec:commons-codec:1.16.0") // Latest as of 2023 August 22
    api("com.google.guava:guava:32.1.2-jre") // Latest as of 2023 August 21
    api("com.google.code.gson:gson:2.10.1") // Latest as of 2023 August 21

    // Sound Libraries (Pending removal by implementation of LWJGL 3.)
    implementation("com.paulscode.sound:libraryjavasound:20101123")
    implementation("com.paulscode.sound:codecwav:20101023")
    implementation("com.paulscode.sound:soundsystem:20120107")
    implementation("com.paulscode.sound:codecjorbis:20101023")
    implementation("com.paulscode.sound:librarylwjglopenal:20100824")

    // Mojang
    implementation("com.ibm.icu:icu4j:51.2") // Neutral

    api("com.mojang:authlib:1.5.21") // Neutral

    // Microsoft account support
    implementation("com.github.CCBlueX:Elixir:1.2.6") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
        exclude(group = "com.mojang", module = "authlib")
    }

    // Testing (useless for now)
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

group = "spoiligaming"

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

tasks.jar {
/*  ↓  To change the name of the JAR file, modify the string below. ←  */
    //                     ↓ ↓ ↓
    archiveBaseName.set("Evanescent")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    exclude("META-INF/**")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    //options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
}

tasks.withType<JavaExec> {
    val osType = if (System.getProperty("os.name").lowercase().contains("windows")) "windows" else "linux"
    systemProperty("java.library.path", "../test_natives/$osType")
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
