// ColorEnum.h
#ifndef COLORENUM_H
#define COLORENUM_H

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

std::string to_string(ColorEnum color);

#endif //COLORENUM_H