package gameai;

import static gameai.Vars.SCORE_FONT;

import processing.core.PApplet;

public class AppletTrain extends PApplet {
	private static final int WIDTH = 1600;
	private static final int HEIGHT = 900;
	private static final int FPS = 60;

	Game g;
	Population p;
	private boolean isViewing = false;
	private boolean wantsView = false;

	public void settings() {
		size(WIDTH, HEIGHT);
	}

	public void setup() {
		frameRate(FPS);
		Vars.initVars(this);
		p = new Population();

		noStroke();
	}

	public void draw() {
		if (p.finishedSim()) {
			if (wantsView) {
				if (!isViewing) {
					g = new Game(0, 0, WIDTH, HEIGHT);
					isViewing = true;
				}
				if (!g.isPlaying) {
					isViewing = false;
				}
			} else {
				isViewing = false;
			}
			if (!isViewing) {
				p.oneGeneration();
			}
		}

		update();
		render();

	}

	private void update() {
		if (isViewing) {
			g.update(p.getBest().predict(g));
		}
	}

	private void render() {
		if (isViewing) {
			g.render(this);
		} else {
			background(255);
			fill(0);
			textFont(SCORE_FONT);
			textSize(60);
			textAlign(CENTER);
			text("Generation: " + p.generations, WIDTH / 2, HEIGHT / 2);
		}
	}

	public void keyPressed() {
		if (key == ' ') {
			wantsView = !wantsView;
		}
	}

}
