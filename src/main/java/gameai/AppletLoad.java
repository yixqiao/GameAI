package gameai;

import processing.core.PApplet;

public class AppletLoad extends PApplet {
	private static final int WIDTH = 1600;
	private static final int HEIGHT = 900;
	private static final int FPS = 60;

	private static final String MODEL_PATH = "demo-150.mdl";

	Game g;
	NeuralNet nn;

	public void settings() {
		size(WIDTH, HEIGHT);
		g = new Game(0, 0, WIDTH, HEIGHT);
		nn = new NeuralNet(MODEL_PATH);
	}

	public void setup() {
		frameRate(FPS);
		Vars.initVars(this);
		noStroke();
	}

	public void draw() {
		update();
		render();
	}

	private void update() {
		if (g.isPlaying) {
			g.update(nn.predict(g));
		}
	}

	private void render() {
		g.render(this);
	}

	public void keyPressed() {
		if (key == ' ') {
			if (!g.isPlaying) {
				g = new Game(0, 0, WIDTH, HEIGHT);
				nn = new NeuralNet(MODEL_PATH);
			}
		}
	}

}
