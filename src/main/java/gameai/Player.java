package gameai;

import processing.core.PApplet;

public class Player extends Entity {

	private static final float START_Y = 500;
	private static final float MOVE_AMOUNT = 0.7f;
	private static final float MOMENTUM = 0.95f;
	private static final float GRAVITY = 1f;
	private static final float STOMP_SPEED = 100;
	private static final int TIME_BETWEEN_STOMPS = 6;

	private float bounceUpSpeed = -32.0f; // Must update if anything is changed

	public int lastStompState = 0;

	boolean isStomping = false;
	private int lastStomp = 0;
	private boolean dDown = false;

	public Player(Game game) {
		super();
		w = 50;
		h = 100;
		x = (game.w - w) / 2;
		y = game.h - h - START_Y;
	}

	public void update(Game game, KeyInput keyInput) {
		if (keyInput.d) {
			if (game.timeTicks - lastStomp >= TIME_BETWEEN_STOMPS && !dDown) {
				isStomping = true;
				lastStomp = game.timeTicks;
			}
			dDown = true;
		} else {
			dDown = false;
		}

		if (isStomping) {
			stomp(game);
			return;
		}

		if (keyInput.l)
			mx -= MOVE_AMOUNT;
		if (keyInput.r)
			mx += MOVE_AMOUNT;

		mx *= MOMENTUM;

		if (y + h >= game.h) {
			my = bounceUpSpeed;
			game.stompStreak = 0;
		} else
			my += GRAVITY;

		x += mx;
		y += my;

		if (x < 0 || x + w >= game.w) {
			x = Math.max(x, 0);
			x = Math.min(x, game.w - w);
			mx = -mx;
			game.isPlaying = false;
		}
	}

	private void stomp(Game game) {
		y += STOMP_SPEED;

		mx *= MOMENTUM;
		x += mx;

		if (y + h >= game.h) {
			finishStomp(game);
			lastStompState = 2;
			game.stompStreak = 0;
		}
	}

	private void finishStomp(Game game) {
		y = Math.min(y, game.h - h - 1);
		mx = 0;
		my = bounceUpSpeed;
		isStomping = false;
	}

	public int collide(Game game) {
		for (int i = 0; i < game.enemies.size(); i++) {
			Enemy e = game.enemies.get(i);
			if (x < e.x + e.w && x + w > e.x && y < e.y + e.h && y + h > e.y) {
				if (isStomping) {
					y = e.y - h;
					lastStompState = 1;
					finishStomp(game);
					return i;
				} else
					return -1;
			}
		}
		return -2;
	}

	@Override
	public void render(Game game, PApplet applet) {
		applet.fill(0, 120, 120);
		applet.rect(game.x + x, game.y + y, w, h);
	}

}
