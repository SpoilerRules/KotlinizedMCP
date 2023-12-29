#include <string>

enum class ColorEnum {
	RESET,
	GREEN,
	DARK_GREEN,
	ERROR_RED,
	LIGHT_BLUE,
	WHITE,
	RED,
	YELLOW
};

std::string to_string(const ColorEnum color) {
	switch (color) {
		case ColorEnum::RESET: return "\033[0m";
		case ColorEnum::GREEN: return "\033[32m";
		case ColorEnum::DARK_GREEN: return "\u001B[32;1m";
		case ColorEnum::ERROR_RED: return "\033[31m";
		case ColorEnum::LIGHT_BLUE: return "\033[94m";
		case ColorEnum::WHITE: return "\033[97m";
		case ColorEnum::RED: return "\033[91m";
		case ColorEnum::YELLOW: return "\033[33m";
		default: return "\033[0m";
	}
}