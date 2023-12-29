#include <cstdint>
#include <filesystem>
#include <memory>
#include <optional>
#include <tuple>
#include <variant>
#include <vector>
#include <Windows.h>

#include "print/Logger.h"
#include "wizard/stages/FirstStage.h"

int main(int argc, char* argv[]) {
    w_fipt::file_path_utils::argumentVector = *argv;

    // forcely enable ansi support
    const auto consoleOutputHandle = GetStdHandle(STD_OUTPUT_HANDLE);
    DWORD currentConsoleMode = 0;
    GetConsoleMode(consoleOutputHandle, &currentConsoleMode);
    SetConsoleMode(consoleOutputHandle, currentConsoleMode | ENABLE_VIRTUAL_TERMINAL_PROCESSING);

    SetConsoleTitle("Setup Wizard | beta-1a");

    for (std::vector<std::tuple<std::variant<w_fipt::file_path_utils::FoldersToExist, std::string>, std::string, std::optional<std::tuple<std::string, int>>>> items = {
             {w_fipt::file_path_utils::FoldersToExist::MCP, "The MCP folder", std::nullopt},
             {w_fipt::file_path_utils::FoldersToExist::JSON_FILE, "Evanescent.json file", std::nullopt},
             {std::string("settings.gradle.kts"), "Evanescent", std::make_optional(std::make_tuple("Evanescent", 1))},
             {std::string("build.gradle.kts"), "Evanescent", std::make_optional(std::make_tuple("Evanescent", 137))},
             {std::string("src/main/kotlin/net/minecraft/util/client/ClientBrandEnum.kt"), "Evanescent | Minecraft 1.8.9", std::make_optional(std::make_tuple("Evanescent | Minecraft 1.8.9", 4))},
             {std::string("src/main/kotlin/net/minecraft/util/client/ClientBrandEnum.kt"), "vanilla", std::make_optional(std::make_tuple("vanilla", 5))}
         }; const auto& [item, name, params] : items) {
        if (std::holds_alternative<w_fipt::file_path_utils::FoldersToExist>(item)) {
            if (auto folder = std::get<w_fipt::file_path_utils::FoldersToExist>(item); !w_fipt::file_path_utils::isFolderExistent(folder)) {
                std::string error_message;
                error_message.append(name)
                             .append(" was not found in the following directory: ")
                             .append(to_string(ColorEnum::WHITE))
                .append(folder == w_fipt::file_path_utils::FoldersToExist::JSON_FILE ?
               w_fipt::file_path_utils::getJSONPath().string() :
               w_fipt::file_path_utils::getMCPPath().string())
                             .append(to_string(ColorEnum::RESET));
                Logger::printError(error_message);
                CasualPrinter::forceExit(10);
            }
        } else {
            if (params && !w_fictu::file_content_utils::verify_content_s(
                w_fipt::file_path_utils::getMCPPath() / std::get<std::string>(item), std::get<0>(*params), std::get<1>(*params))) {
                Logger::printError(
                    "Please launch this setup wizard only when you have not made any modifications to the files inside the MCP folder: "
                    +
                    to_string(ColorEnum::WHITE) + std::get<std::string>(item) + " was modified" + to_string(ColorEnum::RESET) + "."
                );
                CasualPrinter::forceExit(10);
                }
        }
    }

    std::make_unique<q_stage::FirstStage>()->startAsking();
    CasualPrinter::printExit();

    return 0;
}
