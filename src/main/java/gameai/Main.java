package gameai;

import processing.core.PApplet;

public class Main {
	public static void main(String[] args) {
		//gameai.Population.clearSavedModels();
		//System.out.println();
		
		PApplet.main(gameai.AppletPlay.class.getName());
		//gameai.Population p = new gameai.Population();
		//p.simulateAll();
	}
}