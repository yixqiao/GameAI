package gameai;

import processing.core.PApplet;

public class Main {
	public static void main(String[] args) {
		final String option = "watch"; // play, watch, or train

		if(option=="play"){
			PApplet.main(gameai.AppletPlay.class.getName());
		}else if(option=="watch"){
			PApplet.main(gameai.AppletLoad.class.getName());
		}else if(option=="train"){
			PApplet.main(gameai.AppletTrain.class.getName());
		}

		//gameai.Population.clearSavedModels();
	}
}