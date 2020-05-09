package gameai;

import processing.core.PApplet;

public class Enemy extends Entity {
	private static final float GRAVITY = 1f;
	private boolean isRight;

	public Enemy(Game game, boolean isRight, float w, float h, float startY, float moveSpeed) {
		super();
		this.w = w;
		this.h = h;
		this.isRight = isRight;

		if (isRight) {
			x = -w;
			mx = moveSpeed;
		} else {
			x = game.w;
			mx = -moveSpeed;
		}

		y = game.h - h - startY;
	}

	public boolean update(Game game) {
		if (y + h >= game.h)
			my *= -1;
		else
			my += GRAVITY;

		x += mx;
		y += my;

		if ((x < -w && !isRight) || (x >= game.w && isRight)) {
			return true;
		}
		return false;

	}

	@Override
	public void render(Game game, PApplet applet) {
		applet.fill(200, 20, 20);
		applet.rect(game.x + x, game.y + y, w, h);
	}

}
