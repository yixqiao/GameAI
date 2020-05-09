package gameai;

import processing.core.PApplet;

public abstract class Entity {
	public float x, y, w, h;
	protected float mx, my;

	public abstract void render(Game game, PApplet applet);
}
