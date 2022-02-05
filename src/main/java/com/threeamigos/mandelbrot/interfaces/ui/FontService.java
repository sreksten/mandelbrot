package com.threeamigos.mandelbrot.interfaces.ui;

import java.awt.Font;

public interface FontService {

	public static final String STANDARD_FONT_NAME = "Serif";

	Font getFont(String fontName, int fontAttributes, int fontHeight);

}
