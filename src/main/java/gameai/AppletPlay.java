package gameai;

import processing.core.PApplet;

public class AppletPlay extends PApplet {
	private static final int WIDTH = 1600;
	private static final int HEIGHT = 900;
	private static final int FPS = 60;

	Game g;
	KeyInput keyInput;

	public void settings() {
		size(WIDTH, HEIGHT);
	}

	public void setup() {
		frameRate(FPS);
		Vars.initVars(this);
		g = new Game(0, 0, WIDTH, HEIGHT);
		keyInput = new KeyInput();

		noStroke();
	}

	public void draw() {
		update();
		render();
		if (!g.isPlaying) {
			System.out.println(g.score);
			g = new Game(0, 0, WIDTH, HEIGHT);
		}

	}

	public void keyPressed() {
		if (key == CODED) {
			switch (keyCode) {
			case UP:
				keyInput.u = true;
				break;
			case DOWN:
				keyInput.d = true;
				break;
			case LEFT:
				keyInput.l = true;
				break;
			case RIGHT:
				keyInput.r = true;
				break;
			}
		}
	}

	public void keyReleased() {
		if (key == CODED) {
			switch (keyCode) {
			case UP:
				keyInput.u = false;
				break;
			case DOWN:
				keyInput.d = false;
				break;
			case LEFT:
				keyInput.l = false;
				break;
			case RIGHT:
				keyInput.r = false;
				break;
			}
		}
	}

	private void update() {
		g.update(keyInput);
	}

	private void render() {
		g.render(this);
	}
}
