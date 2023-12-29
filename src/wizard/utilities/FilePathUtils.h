//
// Created by spoil on 12/19/2023.
//

#ifndef FILEPATHUTILS_H
#define FILEPATHUTILS_H

#include <filesystem>

namespace w_fipt {
    class file_path_utils {
    public:
        static char* argumentVector;

        enum FoldersToExist {
            CURRENT,
            MCP,
            JSON_FILE
        };

        static std::filesystem::path getCurrentPath();
        static std::filesystem::path getMainDirectory();
        static std::filesystem::path getMCPPath();
        static std::filesystem::path getJSONPath();

        static bool isFolderExistent(const FoldersToExist& w_folder);
    };
}

#endif //FILEPATHUTILS_H
