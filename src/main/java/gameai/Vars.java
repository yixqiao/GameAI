package gameai;

import java.util.Random;

import processing.core.PApplet;
import processing.core.PFont;

public final class Vars {
	public static Random random = new Random();
	public static PFont SCORE_FONT;
	
	public static void initVars(PApplet applet) {
		SCORE_FONT = applet.createFont("Ubuntu Mono Bold", 60);
	}
}
