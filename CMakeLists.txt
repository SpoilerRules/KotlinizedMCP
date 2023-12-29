cmake_minimum_required(VERSION 3.27)
project(KotlinizedMCP_Setup_Wizard)

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_INTERPROCEDURAL_OPTIMIZATION_RELEASE TRUE)

set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/compiled_release)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/compiled_release/libraries)

set(CMAKE_TOOLCHAIN_FILE "C:/Users/spoil/vcpkg/scripts/buildsystems/vcpkg.cmake")

list(APPEND CMAKE_PREFIX_PATH "C:/Users/spoil/vcpkg/installed/x64-windows/share/curl")
list(APPEND CMAKE_PREFIX_PATH "C:/Users/spoil/vcpkg/installed/x64-windows/share/miniz")
list(APPEND CMAKE_PREFIX_PATH "C:/Users/spoil/vcpkg/installed/x64-windows/share/zlib")

set(ZLIB_LIBRARY "C:/Users/spoil/vcpkg/installed/x64-windows/lib/zlib.lib")

find_package(CURL REQUIRED)
find_package(miniz CONFIG REQUIRED)
find_package(ZLIB REQUIRED)

add_executable(SetupWizard_x64
        src/main.cpp
        src/wizard/stages/FirstStage.h
        src/print/ansi_color/ColorEnum.cpp
        src/print/ansi_color/ColorEnum.h
        src/print/CasualPrinter.h
        src/wizard/utilities/FilePathUtils.h
        src/wizard/utilities/FilePathUtils.cpp
        src/wizard/utilities/FileContentUtils.h
        src/print/Logger.h
        src/wizard/stages/SecondStage.h
)

# set_target_properties(SetupWizard_x64 PROPERTIES LINK_FLAGS "/MANIFESTUAC:\"level='requireAdministrator' uiAccess='false'\"")

target_link_libraries(SetupWizard_x64 PUBLIC CURL::libcurl)
target_link_libraries(SetupWizard_x64 PRIVATE miniz::miniz)
target_link_libraries(SetupWizard_x64 PRIVATE ZLIB::ZLIB)

add_custom_command(TARGET SetupWizard_x64 POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy_if_different
        "C:/Users/spoil/vcpkg/installed/x64-windows/bin/libcurl.dll"
        "$<TARGET_FILE_DIR:SetupWizard_x64>"
        COMMAND ${CMAKE_COMMAND} -E copy_if_different
        "$<TARGET_FILE:miniz::miniz>"
        "$<TARGET_FILE_DIR:SetupWizard_x64>"
        COMMAND ${CMAKE_COMMAND} -E copy_if_different
        "C:/Users/spoil/vcpkg/installed/x64-windows/bin/zlib1.dll>"
        "$<TARGET_FILE_DIR:SetupWizard_x64>"

        COMMAND attrib +h "$<TARGET_FILE_DIR:SetupWizard_x64>\\*.dll"
)