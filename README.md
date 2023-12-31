# Evanescent - Kotlinized MCP for Minecraft 1.8.9

[![Progress](https://img.shields.io/badge/Progress-1.0%25-yellow.svg)](https://github.com/SpoilerRules/KotlinizedMCP)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-blue.svg?logo=kotlin)](https://kotlinlang.org/)
[![Java](https://img.shields.io/badge/Java-21-blue.svg?logo=java&logoColor=white)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![Gradle](https://img.shields.io/badge/Gradle-8.5-orange.svg?logo=gradle)](https://gradle.org/)
[![OptiFine](https://img.shields.io/badge/OptiFine-Integrated-green.svg)](https://optifine.net/)

## Table of Contents
- [Introduction](#introduction)
- [JDK Version and Dependencies](#jdk-version-and-dependencies)
- [Frequently Asked Questions](#frequently-asked-questions)
- [Built-in Features](#built-in-features)
- [Pending Built-in Features](#pending-built-in-features)
- [Why the Name "Evanescent"?](#why-the-name-evanescent)
- [Community](#community)
- [Contributions](#contributions)
- [License](#license)

## Introduction

Evanescent is a Minecraft client project that takes MCP 1.8.9 and rewrites it in Kotlin/JVM. This rewrite is designed to offer optimized performance, enhanced readability, and improved conciseness. Powered by the Gradle Kotlin DSL, it ensures smooth dependency setup and fast compilation. We've also integrated OptiFine to save developers valuable time and provide native support for IntelliJ IDEA.

## JDK Version and Dependencies

Evanescent uses the latest LTS JDK (currently version 21) and keeps dependencies up to date to ensure the best performance and security.

## Frequently Asked Questions

#### Is Evanescent (KotlinizedMCP) designed for me to create my Minecraft 1.8.9 client, similar to a typical decompiled MCP 1.8.9?
Absolutely! This project is created to aid client developers by providing a user-friendly template. You should consider using Evanescent, also known as KotlinizedMCP, for the following reasons:
- Aims to always use the latest dependencies and support the latest LTS JDK.
- Uses Gradle (Kotlin Script DSL) to simplify setting up dependencies, compiling, and customizing the project, making your development experience smoother.
- The majority of classes being rewritten in Kotlin/JVM to enhance performance, readability, and maintainability while utilizing modern features of Java and Kotlin/JVM.
- Includes built-in features such as in-game Microsoft Login, removal of support for outdated features like Minecraft Realms, integration with OptiFine, and more. Check out the full list of built-in features in the [Built-in Features](#built-in-features) section.

#### Is Evanescent (KotlinizedMCP) based on decompiled MCP 1.8.9?
Yes, Evanescent, also known as KotlinizedMCP, is a modernized version of decompiled MCP (Mod Coder Pack) 1.8.9.

#### Can I use other JVM languages, such as Scala, for development?
Yes, you are welcome to use any JVM languages to work on Evanescent for your independent projects. Please note that our primary focus for contributions to the GitHub repository is in Kotlin/JVM, given the project's name, "KotlinizedMCP". You can find further details on contributing to the project in the [Contributions](#contributions) section.

## Built-in Features

- ✅ Asynchronous screenshot handling process.
- ✅ Command detection is case-insensitive.
- ✅ Simple Microsoft login in-game.
- ✅ Extra Settings, offering options like Hit Delay, Auto Sprint, and more.
- ✅ Removed Minecraft Realms support.
- ✅ Removed Twitch Broadcast support.
- ✅ Removed Demo version support.

## Pending Built-in Features

- ❌ Removed Snooper support.
- ❌ Implemented raw input.
- ❌ Implemented camera shake customization.

## Why the Name "Evanescent"?

The name “Evanescent” means something that fades away quickly. We chose this name because this client is only a starting point for your own project, and you can customize it as you wish.

## Community

Join the discussion, connect with fellow developers, and ask for help or support at our [Discord server](https://discord.gg/nG9UzMGa7k).

## Contributions

Contributions are greatly appreciated and mainly accepted in Kotlin/JVM. Feel free to contribute to the project and help make it even better!

If you are interested in actively contributing and have already made at least one contribution, we encourage you to join our [Discord server](https://discord.gg/nG9UzMGa7k) and request the "Contributor" role. Ensure that your GitHub account is linked to your Discord profile. Finally, you need to visit [this channel](https://discord.com/channels/1153066699453636680/1153415081946783805) to access information about our goals, schedules, and other relevant details. This will help you stay updated and aligned with our project's policies, which may affect the acceptance of your contributions.

## License

This project is licensed under the Evanescent License, Version 1.0. Please review the [LICENSE](https://github.com/SpoilerRules/KotlinizedMCP/blob/main/LICENSE) file for more details.

---

**Note**: We're actively working on improving this project, and our logo and additional features are in the pipeline. Stay tuned for updates!
