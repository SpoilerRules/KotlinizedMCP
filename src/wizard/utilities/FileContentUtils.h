//
// Created by spoil on 12/20/2023.
//

#ifndef FILECONTENTUTILS_H
#define FILECONTENTUTILS_H

#include <algorithm>
#include <filesystem>
#include <fstream>
#include <iostream>
#include <iostream>
#include <miniz.h>
#include <sstream>
#include <string>
#include <string>
#include <curl/curl.h>

namespace w_fictu {
    class file_content_utils {
        static std::string read_file(const std::filesystem::path& path_to_file) {
            const std::ifstream file_in(path_to_file);
            std::stringstream buffer;
            buffer << file_in.rdbuf();
            return buffer.str();
        }

        static void write_file(const std::filesystem::path& path_to_file, const std::string& content) {
            std::ofstream file_out(path_to_file);
            file_out << content;
        }

        template<typename ReplaceOperation>
        static void replace_content(const std::filesystem::path& path_to_file, ReplaceOperation replace_op,
                                    const size_t line_number) {
            const auto content = read_file(path_to_file);

            std::istringstream stream(content);
            std::string line;
            std::stringstream updated_content;

            size_t current_line_number = 0;
            while (std::getline(stream, line)) {
                ++current_line_number;
                if (current_line_number == line_number) {
                    replace_op(line);
                }
                updated_content << line << '\n';
            }

            write_file(path_to_file, updated_content.str());
        }

    public:
        static void replace_content_s(const std::filesystem::path& path_to_file, const std::string& string_to_replace,
                                      const std::string& replacement, const size_t line_number) {
            replace_content(path_to_file, [&string_to_replace, &replacement](std::string& line) {
                size_t position = 0;
                while ((position = line.find(string_to_replace, position)) != std::string::npos) {
                    line.replace(position, string_to_replace.length(), replacement);
                    position += replacement.length();
                }
            }, line_number);
        }

        static void replace_content_c(const std::filesystem::path& path_to_file, const char& char_to_replace,
                                      const char& replacement, const size_t line_number) {
            replace_content(path_to_file, [char_to_replace, replacement](std::string& line) {
                std::ranges::replace(line, char_to_replace, replacement);
            }, line_number);
        }

        /**
         * Verifies the presence of a specified string in a specific line of the file.
         *
         * @param path_to_file The path to the file to be verified.
         * @param string_to_verify The string to be checked for.
         * @param line_number The line number in which to search for the string.
         * @return True if the specified string is found in the specified line, false otherwise.
         */
        static bool verify_content_s(const std::filesystem::path& path_to_file, const std::string& string_to_verify,
                                     const size_t line_number) {
            const auto content = read_file(path_to_file);
            std::istringstream stream(content);

            std::string line;
            size_t current_line_number = 0;
            while (std::getline(stream, line)) {
                ++current_line_number;
                if (current_line_number > line_number) {
                    return false;
                }
                if (current_line_number == line_number && line.find(string_to_verify) != std::string::npos) {
                    return true;
                }
            }

            return false;
        }

        /**
         * Writes a block of data to a file stream. This function is a wrapper around the fwrite function.
         *
         * @note write_fs stands for "write to file stream".
         *
         * @param data_ptr      Pointer to the block of data to be written. This should point to the first element of an array of data.
         * @param block_size    Size of each element in the block, in bytes.
         * @param block_count   Number of elements in the block to be written.
         * @param file_stream   Open file stream where the data block will be written.
         * @return              Number of elements successfully written to the file stream. If an error occurs, this may be less than block_count, and errno is set.
         */
        static size_t write_fs(const void* data_ptr, const size_t block_size, const size_t block_count,
                               FILE* file_stream) {
            return fwrite(data_ptr, block_size, block_count, file_stream);
        }

        static int j_progress_callback(void* ptr, const double total_to_download, const double now_downloaded,
                                       double total_to_upload, double now_uploaded) {
            if (total_to_download <= 0.0)
                return 0;

            const auto perc = now_downloaded / total_to_download * 100;
            std::cout << to_string(ColorEnum::RESET)
                    << "[" << to_string(ColorEnum::GREEN) << "OK" << to_string(ColorEnum::RESET) << "] "
                    << "Downloading content > "
                    << std::fixed << std::setprecision(2) << now_downloaded / (1024 * 1024)
                    << " MB / " << total_to_download / (1024 * 1024) << " MB (" << perc << "%)\r"
                    << std::flush;
            return 0;
        }

        void download_content(const std::filesystem::path& installation_path, const char* url,
                             const bool& print_progress) {
            // NOLINT(*-convert-member-functions-to-static)
            const auto& installationPath = installation_path;
            const auto outfilename = installationPath.string();
            if (const auto curl = curl_easy_init()) {
                FILE* fp;
                if (const auto err = fopen_s(&fp, outfilename.c_str(), "wb"); err == 0) {
                    curl_easy_setopt(curl, CURLOPT_URL, url);
                    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_fs);
                    curl_easy_setopt(curl, CURLOPT_WRITEDATA, fp);
                    if (print_progress) {
                        curl_easy_setopt(curl, CURLOPT_NOPROGRESS, false);
                        curl_easy_setopt(curl, CURLOPT_PROGRESSFUNCTION, j_progress_callback);
                    }
                    if (const auto status = curl_easy_perform(curl); status != CURLE_OK) {
                        fprintf(stderr, "curl_easy_perform() failed: %s\n", curl_easy_strerror(status));
                    }
                    curl_easy_cleanup(curl);
                    fclose(fp);
                } else {
                    Logger::printError("A suspicious error occurred during installation of content.");
                    CasualPrinter::forceExit(5);
                }
            }

            if (print_progress) std::cout << "\n";
        }

        void unzip_content(const std::string& content_message, const std::filesystem::path& zip_file_path,
                          const std::filesystem::path& destination_folder_path, const bool& print_progress) {
            mz_zip_archive zip_archive{};
            if (!mz_zip_reader_init_file(&zip_archive, zip_file_path.string().c_str(), 0)) {
                throw std::runtime_error("Failed to open ZIP file for reading.");
            }

            mz_zip_archive_file_stat file_stat;
            const auto total_files = mz_zip_reader_get_num_files(&zip_archive);

            for (mz_uint file_index = 0; mz_zip_reader_file_stat(&zip_archive, file_index, &file_stat); ++file_index) {
                std::filesystem::path filename(file_stat.m_filename);
                auto outputFilePath = destination_folder_path / filename;

                if (mz_zip_reader_is_file_a_directory(&zip_archive, file_index)) {
                    create_directories(outputFilePath);
                } else {
                    create_directories(outputFilePath.parent_path());
                    if (!mz_zip_reader_extract_to_file(&zip_archive, file_index, outputFilePath.string().c_str(), 0)) {
                        throw std::runtime_error("Failed to extract: " + outputFilePath.string());
                    }
                }

                if (print_progress) {
                    std::cout << "\r" << Logger::createStatus(ColorEnum::GREEN, "OK") << " Extracting " <<
                            content_message
                            << " > " << to_string(ColorEnum::WHITE) << (file_index + 1) * 100 / total_files << "%" <<
                            to_string(ColorEnum::RESET) << std::flush;
                }
            }

            if (print_progress) std::cout << "\n";

            mz_zip_reader_end(&zip_archive);
            std::filesystem::remove(zip_file_path);
        }

        void download_and_unzip(
            const std::string& description,
            const std::filesystem::path& downloadDestination,
            const char* downloadUrl,
            const std::filesystem::path& unzipDestination,
            const std::string& the_content) {
            download_content(downloadDestination, downloadUrl, false);
            unzip_content(description, downloadDestination, unzipDestination, false);
        }
    };
}

#endif //FILECONTENTUTILS_H
