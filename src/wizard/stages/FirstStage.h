//
// Created by spoil on 12/18/2023.
//

#ifndef KOTLINIZEDMCP_SETUP_WIZARD_FIRSTSTAGE_H
#define KOTLINIZEDMCP_SETUP_WIZARD_FIRSTSTAGE_H

#include <cstdint>
#include <iostream>
#include <string>

#include "SecondStage.h"
#include "../../print/CasualPrinter.h"
#include "../utilities/FileContentUtils.h"
#include "../utilities/FilePathUtils.h"

namespace q_stage {
    class FirstStage {
        uint_fast8_t stage = 1;

        static void askQuestion(const std::string& question, const std::string& defaultAnswer, const std::string& filePath, const int line) {
            std::string userInput;
            CasualPrinter::printQuestion(question);
            std::getline(std::cin, userInput);

            if (!userInput.empty() && userInput != defaultAnswer) {
                w_fictu::file_content_utils::replace_content_s(
                    w_fipt::file_path_utils::getMCPPath() / filePath, defaultAnswer,
                    userInput, line
                );
            }
            std::cout << "\n";
        }

    public:
        void startAsking() {
            do {
                switch (stage) {
                    default: Logger::printError("A wild error has been thrown. Please report this to the developer.");
                    case 1:
                        askQuestion("What would you like to name your root project? (Default: \"Evanescent\")", "Evanescent", "settings.gradle.kts", 1);
                        stage = 2;
                        break;

                    case 2:
                        askQuestion("What would you like to name the built JAR file? (Default: \"Evanescent\")", "Evanescent", "build.gradle.kts", 137);
                        stage = 3;
                        break;

                    case 3:
                        askQuestion("What would you like to name the Minecraft window? (Default: \"Evanescent | Minecraft 1.8.9\")", "Evanescent | Minecraft 1.8.9", "src/main/kotlin/net/minecraft/util/client/ClientBrandEnum.kt", 4);
                        stage = 4;
                        break;

                    case 4:
                        askQuestion("What name would you like to assign to the modification status of Minecraft? (Default: \"vanilla\")", "vanilla", "src/main/kotlin/net/minecraft/util/client/ClientBrandEnum.kt", 5);
                        stage = 5;
                        break;

                    case 5:
                        std::make_unique<SecondStage>()->startAsking();
                        stage = 6;
                        break;
                }
            } while (stage < 6);
        }
    };
}

#endif //KOTLINIZEDMCP_SETUP_WIZARD_FIRSTSTAGE_H
