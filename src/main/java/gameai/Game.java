package gameai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import processing.core.PApplet;

import static gameai.Vars.*;

class SortEnemies implements Comparator<Enemy> {
	public int compare(Enemy a, Enemy b) {
		return (int) ((a.x - b.x) * 100);
	}
}

public class Game {
	public int x, y, w, h;
	public Player player;
	public ArrayList<Enemy> enemies;

	public int timeTicks = 0;
	public int score = 0;
	public boolean isPlaying = true;

	public int stompStreak = 0;

	private int untilNextEnemy = 0;
	private int lastEnemy = 0;

	public Game(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;

		player = new Player(this);

		enemies = new ArrayList<Enemy>();
	}

	public void update(KeyInput keyInput) {
		player.update(this, keyInput);
		int collision = player.collide(this);
		if (collision == -1) {
			isPlaying = false;
		}
		if (player.lastStompState != 0) {
			if (player.lastStompState == 1) {
				enemies.remove(collision);
				stompStreak++;
				score += 5 + stompStreak * 3;
			}
			score-=2;
			player.lastStompState = 0;
		}

		if (timeTicks - lastEnemy >= untilNextEnemy) {
			newEnemy();
		}
		for (int i = 0; i < enemies.size(); i++) {
			if (enemies.get(i).update(this)) {
				enemies.remove(i);
			}
		}

		timeTicks++;
		if (timeTicks % 60 == 0)
			score++;
	}

	private void newEnemy() {
		boolean isRight = random.nextBoolean();
		int width = 50;
		int height = 100;
		int startY = random.nextInt(100) - 50 + 350;
		float speed = random.nextFloat() * 8 - 4 + 8;
		enemies.add(new Enemy(this, isRight, width, height, startY, speed));
		untilNextEnemy = random.nextInt(30) - 15 + 90 - (timeTicks / 45);
		lastEnemy = timeTicks;
	}

	public ArrayList<Enemy> getEnemies(int n) {
		ArrayList<Enemy> out = new ArrayList<Enemy>();
		if (enemies.size() == 0)
			return out;
		Collections.sort(enemies, new SortEnemies());
		int pi = -1;
		for (int i = 0; i < enemies.size(); i++) {
			if (enemies.get(i).x >= player.x) {
				pi = i;
				break;
			}
		}
		for (int i = Math.max(pi - n, 0); i < pi + n && i < enemies.size(); i++) {
			out.add(enemies.get(i));
		}
		return out;
	}

	public void render(PApplet applet) {
		applet.fill(255);
		applet.rect(x, y, w, h);

		applet.fill(0);
		applet.textFont(SCORE_FONT);
		applet.textSize(36);
		applet.textAlign(PApplet.RIGHT);
		applet.text(score, w - 10, y + 30);

		player.render(this, applet);
		for (Enemy e : enemies)
			e.render(this, applet);
	}
}
