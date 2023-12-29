//
// Created by spoil on 12/22/2023.
//

#ifndef SECONDSTAGE_H
#define SECONDSTAGE_H

#include <cctype>
#include <chrono>
#include <cstdint>
#include <cstdlib>
#include <future>
#include <iostream>
#include <string>
#include <thread>

#include "../../print/CasualPrinter.h"
#include "../../print/Logger.h"
#include "../utilities/FileContentUtils.h"
#include "../utilities/FilePathUtils.h"

namespace q_stage {
    class SecondStage {
        struct BundledFolderCreator {
            static constexpr auto TIMEOUT_DURATION = std::chrono::minutes(3);
            static constexpr auto SLEEP_DURATION = std::chrono::seconds(1);

            static void createBundledFolder() {
                const auto bundledFolderPath = w_fipt::file_path_utils::getMainDirectory() / "bundled-verf" / "Evanescent";

                if (!exists(bundledFolderPath)) create_directories(bundledFolderPath);

                const auto buildJarFuture = std::async(std::launch::async, &buildJar, bundledFolderPath / "Evanescent.jar");
                const auto copyJsonFileFuture = std::async(std::launch::async, &copyJsonFile, bundledFolderPath / "Evanescent.json");

                buildJarFuture.wait();
                copyJsonFileFuture.wait();
            }

            static void buildJar(const std::filesystem::path& destinationPath) {
                const auto sourcePath = w_fipt::file_path_utils::getMCPPath() / "build" / "libs" / "Evanescent.jar";

                if (!exists(sourcePath)) {
                    remove_all(w_fipt::file_path_utils::getMCPPath() / ".gradle");

                    const auto command = "start /D \"" + w_fipt::file_path_utils::getMCPPath().string() +
                                         "\" cmd.exe /C gradlew.bat clean build && exit";
                    system(command.c_str());

                    const auto start_time = std::chrono::steady_clock::now();

                    while (!exists(sourcePath)) {
                        std::this_thread::sleep_for(std::chrono::seconds(SLEEP_DURATION));
                        if (std::chrono::steady_clock::now() - start_time > TIMEOUT_DURATION) {
                            Logger::printError("Abandoned task routine: Evanescent.jar not found within 3 minutes.");
                            CasualPrinter::forceExit(5);
                        }
                    }
                } else Logger::printWarning("Using already existing Evanescent.jar file to create a bundled version folder.");

                std::filesystem::rename(sourcePath, destinationPath);
            }

            static void copyJsonFile(const std::filesystem::path& destinationPath) {
                try {
                    copy_file(w_fipt::file_path_utils::getJSONPath(), destinationPath,
                              std::filesystem::copy_options::skip_existing);
                } catch (const std::filesystem::filesystem_error& e) {
                    Logger::printError(
                        "An error occurred while copying the JSON file into the bundled version folder: " +
                        std::string(e.what()));
                }
            }
        };

        static void createWorkspace() {
            const auto fileContentUtils = std::make_unique<w_fictu::file_content_utils>();

            fileContentUtils->download_content(
                w_fipt::file_path_utils::getMCPPath() / "workspace" / "workspace.zip",
                "https://cdn.discordapp.com/attachments/1187894207680090183/1188060973291020319/workspace.zip?ex=65992701&is=6586b201&hm=147d08f55575235d2187e39c2574000099891913774d5a8467e889cc03fd159b&",
                true);
            fileContentUtils->unzip_content("low-size workspace ZIP",
                                            w_fipt::file_path_utils::getMCPPath() / "workspace" / "workspace.zip",
                                            w_fipt::file_path_utils::getMCPPath() / "workspace", true);

            Logger::printSuccess("Downloading more content for workspace creation... This may take a while.");
            const auto task1 = std::async(std::launch::async, [&] {
                return fileContentUtils->download_and_unzip(
                    "high-size workspace ZIP (a)",
                    w_fipt::file_path_utils::getMCPPath() / "workspace" / "objects1.zip",
                    "https://cdn.discordapp.com/attachments/1187894207680090183/1188523463565119498/objects1.zip?ex=659ad5bb&is=658860bb&hm=6172f1f0675a87647cad59678fb4a37ec4a242aa432d63811515bbc2f66f4887&",
                    w_fipt::file_path_utils::getMCPPath() / "workspace" / "assets" / "objects",
                    "high-size workspace content (a)");
            });

            const auto task2 = std::async(std::launch::async, [&] {
                return fileContentUtils->download_and_unzip(
                    "high-size workspace ZIP (b)",
                    w_fipt::file_path_utils::getMCPPath() / "workspace" / "objects2.zip",
                    "https://cdn.discordapp.com/attachments/1187894207680090183/1188523972929802290/objects2.zip?ex=659ad634&is=65886134&hm=2f28ef99f823245cc13d46456a06946cb060408879e547760b980c38dd39a23f&",
                    w_fipt::file_path_utils::getMCPPath() / "workspace" / "assets" / "objects",
                    "high-size workspace content (b)");
            });

            const auto task3 = std::async(std::launch::async, [&] {
                return fileContentUtils->download_and_unzip(
                    "high-size workspace ZIP (c)",
                    w_fipt::file_path_utils::getMCPPath() / "workspace" / "objects3.zip",
                    "https://cdn.discordapp.com/attachments/1187894207680090183/1188524563580067900/objects3.zip?ex=659ad6c1&is=658861c1&hm=ff2e58d19dba908ff2d80618cc9b5a26232cd6ae67cba8fec7a9e98dc12c9efb&",
                    w_fipt::file_path_utils::getMCPPath() / "workspace" / "assets" / "objects",
                    "high-size workspace content (c)");
            });

            Logger::printSuccess("Downloading additional content for workspace. This process may take a while. (2.2 GB)");
            task1.wait();
            task2.wait();
            task3.wait();

            Logger::printSuccess("Successfully set up a workspace.");
        }

        uint_fast8_t stage = 1;

        static bool askUser(const std::string& question) {
            for (std::string userInput; CasualPrinter::printQuestion(question), std::getline(std::cin, userInput);) {
                if (!userInput.empty()) {
                    if (const auto lowered = std::tolower(userInput[0]); lowered != EOF) {
                        switch (static_cast<char>(lowered)) {
                            case 'y': return true;
                            case 'n': return false;
                            default: Logger::printWarning("Invalid input. Please enter 'y' for yes or 'n' for no.");
                        }
                    }
                }
            }

            std::cout << "\n";
            return false;
        }

    public:
        void startAsking() {
            do {
                switch (stage) {
                    default:
                        Logger::printError("A wild error has been thrown. Please report this to the developer.");
                        break;
                    case 1:
                        if (!exists(w_fipt::file_path_utils::getMCPPath() / "workspace")) {
                            if (askUser("Would you like to create a working directory? (y/n)")) {
                                create_directory(w_fipt::file_path_utils::getMCPPath() / "workspace");
                                createWorkspace();
                            }
                        } else {
                            Logger::printWarning(
                                "Skipping workspace creation because the \"workspace\" folder inside the MCP folder is already present.");
                        }
                        stage = 2;
                        break;

                    case 2:
                        if (askUser("Would you like to create a bundled version folder for Evanescent? (y/n)")) {
                            std::make_unique<BundledFolderCreator>()->createBundledFolder();
                        }
                        stage = 3;
                        break;
                }
            } while (stage < 3);
        }
    };
}

#endif //SECONDSTAGE_H
