//
// Created by spoil on 12/11/2023.
//

#ifndef CASUALPRINTER_H
#define CASUALPRINTER_H

#include <iostream>

#include "ansi_color/ColorEnum.h"

class CasualPrinter {
public:
	template <typename V>
	static void printQuestion(const V& question) {
		printUserInput(ColorEnum::GREEN, "QUESTION", question);
	}

	static void printExit() {
		std::cout << to_string(ColorEnum::RESET) + "[" + to_string(ColorEnum::YELLOW) + "WARNING" + to_string(ColorEnum::RESET) + "] ";
		system("pause");
	}

	static void forceExit(const u_int secondsToWait) {
		for (auto i = secondsToWait; i > 0; --i) {
			std::cout << "\r"
			<< to_string(ColorEnum::RESET) + "[" + to_string(ColorEnum::YELLOW) + "WARNING" + to_string(ColorEnum::RESET) + "]"
			<< " "
			<< to_string(ColorEnum::RESET) + "Exiting in "
			<< to_string(ColorEnum::RED) << i << to_string(ColorEnum::RESET) + " seconds.."
			<< std::flush;
			std::this_thread::sleep_for(std::chrono::seconds(1));
		}

		exit(0);
	}

private:
	static std::string createStatus(const ColorEnum primaryColor, const std::string& status) {
		return to_string(ColorEnum::RESET) + "[" + to_string(primaryColor) + status + to_string(ColorEnum::RESET) + "]" + to_string(ColorEnum::RESET);
	}

	template <typename V>
	static void printLine(const ColorEnum color, const std::string& status, const V& output) {
		std::cout << createStatus(color, status) << " " << output << '\n';
	}

	template <typename V>
	static void print(const ColorEnum color, const std::string& status, const V& output) {
		std::cout << createStatus(color, status) << " " << output;
	}

	template <typename V>
	static void printUserInput(const ColorEnum color, const std::string& status, const V& output) {
		std::cout << createStatus(color, status) << " " << output << " > " << to_string(ColorEnum::DARK_GREEN);
	}
};

#endif //CASUALPRINTER_H
