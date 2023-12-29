#ifndef LOGGER_H
#define LOGGER_H

#include <iostream>
#include <string>

#include "ansi_color/ColorEnum.h"

class Logger {
public:
	template <typename V>
	static void printError(const V& error) {
		printMessage(ColorEnum::ERROR_RED, "ERROR", error);
	}

	template <typename V>
	static void printSuccess(const V& message) {
		printMessage(ColorEnum::GREEN, "OK", message);
	}

	template <typename V>
	static void printWarning(const V& warning) {
		printMessage(ColorEnum::YELLOW, "WARNING", warning);
	}

	static std::string createStatus(const ColorEnum primaryColor, const std::string& status) {
		return to_string(ColorEnum::RESET) + "[" + to_string(primaryColor) + status + to_string(ColorEnum::RESET) + "]" + to_string(ColorEnum::RESET);
	}

private:
	template <typename V>
	static void printMessage(const ColorEnum color, const std::string& status, const V& message) {
		std::cout << createStatus(color, status) << " " << message << '\n';
	}
};

#endif