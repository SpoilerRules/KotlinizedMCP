#include "FilePathUtils.h"

#include <filesystem>

#include "../../print/CasualPrinter.h"

namespace w_fipt {
    char* file_path_utils::argumentVector = nullptr;

    std::filesystem::path file_path_utils::getCurrentPath() {
        return canonical(std::filesystem::path(std::string(argumentVector)));
    }

    std::filesystem::path file_path_utils::getMainDirectory() {
        auto currentPath = getCurrentPath();

        for (auto i = 0; i < 3; ++i) {
            currentPath = currentPath.parent_path();
        }

        return currentPath;
    }

    std::filesystem::path file_path_utils::getMCPPath() {
        return getMainDirectory() / "mcp";
    }

    std::filesystem::path file_path_utils::getJSONPath() {
        return getMainDirectory() / "launch-ready" / "Evanescent.json";
    }

    bool file_path_utils::isFolderExistent(const FoldersToExist& w_folder) {
        switch (w_folder) {
            case CURRENT:
                return exists(std::filesystem::current_path());
            case MCP:
                return exists(getMCPPath());
            case JSON_FILE:
                return exists(getJSONPath());
        }

        return false;
    }
}
