# Evanescent - Kotlinized MCP for Minecraft 1.8.9

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.8.9-brightgreen.svg)](https://www.minecraft.net/)

## Table of Contents
- [Introduction](#introduction)
- [Quick Usage Guide](#quick-usage-guide)
- [Contributions](#contributions)
- [Community](#community)
- [License](#license)

## Introduction

This branch is designed to aid developers by providing a pre-built [JSON file](https://github.com/SpoilerRules/KotlinizedMCP/blob/launch-ready/Evanescent.json) for the [Minecraft Launcher](https://www.minecraft.net/en-us/download). The [JSON file](https://github.com/SpoilerRules/KotlinizedMCP/blob/launch-ready/Evanescent.json) includes all necessary dependencies, arguments, and required data. This branch maintains strict compatibility with the [MCP branch](https://github.com/SpoilerRules/KotlinizedMCP/tree/mcp) to prevent any compatibility issues.

# Quick Usage Guide

1. Compile the [MCP](https://github.com/SpoilerRules/KotlinizedMCP/tree/mcp) to obtain the [JAR](https://en.wikipedia.org/wiki/JAR_(file_format)) file.
2. Create a folder named "Evanescent" anywhere you prefer.
3. Place the [JAR](https://en.wikipedia.org/wiki/JAR_(file_format)) file and the [JSON file](https://github.com/SpoilerRules/KotlinizedMCP/blob/launch-ready/Evanescent.json) in the "Evanescent" folder.
4. Move the "Evanescent" folder to the "versions" directory in your .minecraft folder.
5. Test to ensure functionality and observe the results.

Note: If you've modified the [source code](https://github.com/SpoilerRules/KotlinizedMCP/tree/mcp), you may need to adjust elements like the [ID](https://github.com/SpoilerRules/KotlinizedMCP/blob/launch-ready/Evanescent.json#L2) or manage dependencies accordingly.

## Contributions

We greatly appreciate contributions to this project and aim to maintain consistency and quality. To ensure a smooth contribution process, please adhere to the following guidelines:

1. **Dependency Inclusion**:
    - Ensure that any dependencies you add are listed in the [Gradle Build file](https://github.com/SpoilerRules/KotlinizedMCP/blob/mcp/build.gradle.kts) from the [MCP branch](https://github.com/SpoilerRules/KotlinizedMCP/tree/mcp).
    - Make sure to remove dependencies that are no longer necessary, and ensure they are removed from the [Gradle Build file](https://github.com/SpoilerRules/KotlinizedMCP/blob/mcp/build.gradle.kts) as well.

2. **Version Consistency**:
    - Verify that any dependencies you add are consistent with the versions specified in the [Gradle Build file](https://github.com/SpoilerRules/KotlinizedMCP/blob/mcp/build.gradle.kts) from the [MCP branch](https://github.com/SpoilerRules/KotlinizedMCP/tree/mcp).

3. **Excluding Test Dependencies**:
    - Do not add test-related dependencies such as [JUnit](https://junit.org/).

4. **Testing and Validation**:
    - Before submitting a pull request, thoroughly test and debug how the [Minecraft Launcher](https://www.minecraft.net/en-us/download) reacts to the [JSON file](https://github.com/SpoilerRules/KotlinizedMCP/blob/launch-ready/Evanescent.json). Ensure that your changes do not introduce any issues.

By following these guidelines, you can help maintain the integrity and compatibility of this project. Thank you for your contributions!

## Community

If you have questions or wish to engage in discussions related to the project, we encourage you to join our [Discord server](https://discord.gg/nG9UzMGa7k).

## License

This project is licensed under the Evanescent License, Version 1.0. Please review the [LICENSE](https://github.com/SpoilerRules/KotlinizedMCP/blob/main/LICENSE.md) file for more details.

---

**Note**: We're actively working on improving this project, and our logo and additional features are in the pipeline. Stay tuned for updates!
